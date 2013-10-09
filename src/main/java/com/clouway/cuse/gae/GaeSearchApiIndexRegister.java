package com.clouway.cuse.gae;

import com.clouway.cuse.gae.exceptions.ErrorDeletingIndexException;
import com.clouway.cuse.gae.exceptions.InvalidDocumentIdException;
import com.clouway.cuse.gae.exceptions.InvalidFieldValueException;
import com.clouway.cuse.gae.exceptions.InvalidIndexNameException;
import com.clouway.cuse.gae.exceptions.UnableToCreateSearchServiceException;
import com.clouway.cuse.spi.IndexRegister;
import com.clouway.cuse.spi.IndexWriter;
import com.clouway.cuse.spi.IndexingSchema;
import com.clouway.cuse.spi.IndexingStrategy;
import com.google.appengine.api.search.DeleteException;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.SearchService;
import com.google.appengine.api.search.SearchServiceFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class GaeSearchApiIndexRegister implements IndexRegister {
  @Override
  public void register(Object instance, IndexingStrategy strategy) {
    String documentId = strategy.getId(instance);

    IndexingSchema indexingSchema = strategy.getIndexingSchema();
    List<String> fields = indexingSchema.getFields();
    List<String> fullTextFields = indexingSchema.getFullText();

    Document document = buildDocument(instance, documentId, fields, fullTextFields);

    addDocumentInIndex(strategy.getIndexName(), document);
  }

  public void delete(String indexName, List<Long> objectIds){
    List<String> stringObjectIds = new ArrayList<String>();
    for (Long id : objectIds) {
      stringObjectIds.add(String.valueOf(id));
    }

    try {
      loadIndex(indexName).delete(stringObjectIds);
    } catch (IllegalArgumentException e) {
      throw new InvalidDocumentIdException();
    } catch (DeleteException e) {
      throw new ErrorDeletingIndexException();
    }
  }

  private Index loadIndex(String indexName) {

    IndexSpec indexSpec;

    try {
      indexSpec = IndexSpec.newBuilder().setName(indexName).build();
    } catch (IllegalArgumentException e) {
      throw new InvalidIndexNameException();
    }

    SearchService searchService;

    try {

      searchService = SearchServiceFactory.getSearchService();

    } catch (IllegalArgumentException e) {
      throw new UnableToCreateSearchServiceException();
    }

    return searchService.getIndex(indexSpec);
  }

  private void addDocumentInIndex(String indexName, Document document) {
    loadIndex(indexName).put(document);
  }

  private Document buildDocument(Object instance, String documentId, List<String> fields, List<String> fullTextFields) {

    Document.Builder documentBuilder;

    try {
      documentBuilder = Document.newBuilder().setId(documentId);
    } catch (IllegalArgumentException e) {
      throw new InvalidDocumentIdException();
    }

    Map<String, String> documentFields = new HashMap<String, String>();
    for (java.lang.reflect.Field field : instance.getClass().getDeclaredFields()) {

      if (fields.contains(field.getName())) {
        String fieldValue = getFieldValue(instance, field);
        documentFields.put(field.getName(), fieldValue);
      }

      if (fullTextFields.contains(field.getName())) {
        Set<String> fieldValues = new IndexWriter().createIndex(getFieldValue(instance, field));
        for (String fieldValue : fieldValues) {

          try {
            documentBuilder.addField(Field.newBuilder().setName(field.getName()).setText(fieldValue));
          } catch (IllegalArgumentException e) {
            throw new InvalidFieldValueException();
          }
        }
      }
    }

    for (String field : documentFields.keySet()) {
      String fieldValue = documentFields.get(field);

      try {
        documentBuilder.addField(Field.newBuilder().setName(field).setText(fieldValue));
      } catch (IllegalArgumentException e) {
        throw new InvalidFieldValueException();
      }
    }

    return documentBuilder.build();
  }

  private String getFieldValue(Object instance, java.lang.reflect.Field field) {

    String fieldValue = "";

    try {

      java.lang.reflect.Field declaredField = instance.getClass().getDeclaredField(field.getName());
      declaredField.setAccessible(true);
      fieldValue = String.valueOf(declaredField.get(instance));

    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }

    return fieldValue;
  }
}

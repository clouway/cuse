package com.clouway.cuse.gae;

import com.clouway.cuse.spi.IndexRegister;
import com.clouway.cuse.spi.IndexWriter;
import com.clouway.cuse.spi.IndexingSchema;
import com.clouway.cuse.spi.IndexingStrategy;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.SearchServiceFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    loadIndex(indexName).delete(stringObjectIds);
  }

  private Index loadIndex(String indexName) {
    return SearchServiceFactory.getSearchService().getIndex(IndexSpec.newBuilder()
            .setName(indexName));
  }

  private void addDocumentInIndex(String indexName, Document document) {
    loadIndex(indexName).put(document);
  }

  private Document buildDocument(Object instance, String documentId, List<String> fields, List<String> fullTextFields) {

    Document.Builder documentBuilder = Document.newBuilder().setId(documentId);

    for (java.lang.reflect.Field field : instance.getClass().getDeclaredFields()) {

      Object fieldValue = getFieldValue(instance, field);

      if (fields.contains(field.getName())) {

        if (field.getType().equals(Date.class)) {
          buildDateField(documentBuilder, field, (Date) fieldValue);
        } else {
          buildTextField(documentBuilder, field, fieldValue);
        }
      }

      if (fullTextFields.contains(field.getName())) {

        Set<String> fieldValues = new IndexWriter().createIndex(String.valueOf(fieldValue));

        for (String value : fieldValues) {
          documentBuilder.addField(Field.newBuilder().setName(field.getName()).setText(value));
        }
      }
    }

    return documentBuilder.build();
  }

  private Object getFieldValue(Object instance, java.lang.reflect.Field field) {

    try {

      java.lang.reflect.Field declaredField = instance.getClass().getDeclaredField(field.getName());
      declaredField.setAccessible(true);

      return declaredField.get(instance);

    } catch (IllegalAccessException e) {
      throw new FieldNotAccessibleException();
    } catch (NoSuchFieldException e) {
      throw new NoFieldOfASpecifiedNameException();
    }
  }

  private void buildDateField(Document.Builder documentBuilder, java.lang.reflect.Field field, Date fieldValue) {

    if (fieldValue == null) {
      documentBuilder.addField(Field.newBuilder().setName(field.getName()).setNumber(0));
    } else {
      Long dateInSeconds = fieldValue.getTime() / 1000;
      documentBuilder.addField(Field.newBuilder().setName(field.getName()).setNumber(dateInSeconds));
    }
  }

  private void buildTextField(Document.Builder documentBuilder, java.lang.reflect.Field field, Object fieldValue) {
    documentBuilder.addField(Field.newBuilder().setName(field.getName()).setText(String.valueOf(fieldValue)));
  }
}

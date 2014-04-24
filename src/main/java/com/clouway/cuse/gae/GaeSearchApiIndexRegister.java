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
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
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

    Document document = buildDocument(instance, documentId, indexingSchema);

    addDocumentInIndex(strategy.getIndexName(), document);
  }

  public void delete(String indexName, List<Long> objectIds) {
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

  private Document buildDocument(Object instance, String documentId, IndexingSchema indexingSchema) {

    Document.Builder documentBuilder = Document.newBuilder().setId(documentId);

    for (java.lang.reflect.Field field : instance.getClass().getDeclaredFields()) {

      Object instanceValue = getFieldValue(instance, field);

      if (containsField(indexingSchema.getFields(), field.getName())) {

        List<DocumentProperty> documentProperties = getDocumentProperty(indexingSchema.getFields(), field, instanceValue);

        for (DocumentProperty property : documentProperties) {
          if (field.getType().equals(Date.class)) {
            buildDateField(documentBuilder, field, (Date) instanceValue);
          } else {
            buildTextField(documentBuilder, property.getName(), property.getValue());
          }
        }
      }

      if (containsField(indexingSchema.getFullText(), field.getName())) {

        List<DocumentProperty> documentProperties = getDocumentProperty(indexingSchema.getFullText(), field, instanceValue);

        for (DocumentProperty property : documentProperties) {
          Set<String> fieldValues = new HashSet<String>();
          IndexWriter indexWriter = new IndexWriter();

          Object propertyValue = property.getValue();

          if (propertyValue != null && propertyValue instanceof Collection) {
            Collection collection = (Collection) propertyValue;
            for (Object object : collection) {
              fieldValues.addAll(indexWriter.createIndex(String.valueOf(object)));
            }
          } else {
            fieldValues.addAll(indexWriter.createIndex(String.valueOf(propertyValue)));
          }

          applyTextValues(documentBuilder, property.name, fieldValues);
        }
      }

      if (containsField(indexingSchema.getWordFields(), field.getName()) && instanceValue != null && instanceValue instanceof String) {

        List<DocumentProperty> fieldValues = getDocumentProperty(indexingSchema.getWordFields(), field, instanceValue);

        for (DocumentProperty fieldValue : fieldValues) {
          String stringValue = (String) fieldValue.getValue();
          String[] splitValues = stringValue.split(" ");

          applyTextValues(documentBuilder, fieldValue.name, Arrays.asList(splitValues));
        }
      }
    }

    return documentBuilder.build();
  }

  private void applyTextValues(Document.Builder documentBuilder, String fieldName, Iterable<String> values) {
    for (String value : values) {
      documentBuilder.addField(Field.newBuilder().setName(fieldName).setText(value));
    }
  }

  private boolean containsField(List<String> fields, String fieldName) {
    for (String rawFieldName : fields) {
      List<String> splitFieldName = Arrays.asList(rawFieldName.split("_"));
      if(splitFieldName.contains(fieldName)) {
        return true;
      }
    }
    return false;
  }

  //todo: use recursion for optimization
  private List<DocumentProperty> getDocumentProperty(List<String> fields, java.lang.reflect.Field field, Object instance) {

    List<DocumentProperty> documentProperties = new ArrayList<DocumentProperty>();

    for (String rawFieldName : fields) {
      String[] splitFieldName = rawFieldName.split("_");

      if (splitFieldName.length == 1) {
        if (splitFieldName[0].equals(field.getName())) {
          documentProperties.add(new DocumentProperty(rawFieldName, instance));
        }
      } else if (splitFieldName.length == 2) {
        if (splitFieldName[0].equals(field.getName()) && instance != null) {
          try {
            java.lang.reflect.Field childField = instance.getClass().getDeclaredField(splitFieldName[1]);
            Object childValue = getFieldValue(instance, childField);
            documentProperties.add(new DocumentProperty(rawFieldName, childValue));
          } catch (NoSuchFieldException e) {
            e.printStackTrace();
          }
        } else if (splitFieldName[0].equals(field.getName()) && instance == null) {
          documentProperties.add(new DocumentProperty(rawFieldName, null));
        }
      }
    }

    return documentProperties;
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

  private void buildTextField(Document.Builder documentBuilder, String filedName, Object fieldValue) {
    documentBuilder.addField(Field.newBuilder().setName(filedName).setText(String.valueOf(fieldValue)));
  }

  private class DocumentProperty {

    private String name;
    private Object value;

    DocumentProperty(String field, Object value) {
      this.name = field;
      this.value = value;
    }

    public String getName() {
      return name;
    }

    public Object getValue() {
      return value;
    }
  }
}

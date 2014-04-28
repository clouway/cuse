package com.clouway.cuse.gae.filedindexing.fulltextindex;

import com.clouway.cuse.gae.filedindexing.FieldIndexer;
import com.clouway.cuse.spi.IndexWriter;
import com.google.appengine.api.search.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.appengine.api.search.Field.newBuilder;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
public class FullTextSearchFieldIndexer implements FieldIndexer {
  @Override
  public List<Field> execute(String fieldName, java.lang.reflect.Field field, Object fieldInstanceValue) {

    ArrayList<Field> fields = new ArrayList<Field>();
    Set<String> fieldValues = new HashSet<String>();
    IndexWriter indexWriter = new IndexWriter();

    if (fieldInstanceValue != null && fieldInstanceValue instanceof Collection) {
      Collection collection = (Collection) fieldInstanceValue;
      for (Object object : collection) {
        fieldValues.addAll(indexWriter.createIndex(String.valueOf(object)));
      }
    } else {
      fieldValues.addAll(indexWriter.createIndex(String.valueOf(fieldInstanceValue)));
    }

    for (String fieldValue : fieldValues) {
      Field newField = newBuilder().setName(fieldName).setText(String.valueOf(fieldValue)).build();
      fields.add(newField);
    }

    return fields;
  }
}

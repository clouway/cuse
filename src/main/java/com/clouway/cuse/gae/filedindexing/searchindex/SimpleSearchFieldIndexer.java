package com.clouway.cuse.gae.filedindexing.searchindex;

import com.clouway.cuse.gae.filedindexing.FieldIndexer;
import com.google.appengine.api.search.Field;

import java.util.ArrayList;
import java.util.List;

import static com.google.appengine.api.search.Field.newBuilder;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
public class SimpleSearchFieldIndexer implements FieldIndexer {

  @Override
  public List<Field> index(String fieldName, java.lang.reflect.Field field, Object fieldInstanceValue) {

    List<Field> fields = new ArrayList<Field>();

    Field documentField;
    if (fieldInstanceValue == null) {
      documentField = newBuilder().setName(fieldName).setText("").build();
    } else {
      documentField = newBuilder().setName(fieldName).setText(String.valueOf(fieldInstanceValue)).build();
    }

    fields.add(documentField);

    return fields;
  }
}

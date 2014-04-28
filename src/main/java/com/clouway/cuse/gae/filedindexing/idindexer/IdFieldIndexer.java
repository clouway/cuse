package com.clouway.cuse.gae.filedindexing.idindexer;

import com.clouway.cuse.gae.filedindexing.FieldIndexer;
import com.google.appengine.api.search.Field;

import java.util.ArrayList;
import java.util.List;

import static com.google.appengine.api.search.Field.newBuilder;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
public class IdFieldIndexer implements FieldIndexer {

  @Override
  public List<Field> index(String fieldName, java.lang.reflect.Field field, Object fieldInstanceValue) {

    List<Field> fields = new ArrayList<Field>();

    Field documentField = newBuilder().setName(fieldName).setText(String.valueOf(fieldInstanceValue)).build();

    fields.add(documentField);

    return fields;
  }
}

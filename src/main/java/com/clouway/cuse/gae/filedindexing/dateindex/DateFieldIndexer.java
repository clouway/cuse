package com.clouway.cuse.gae.filedindexing.dateindex;

import com.clouway.cuse.gae.filedindexing.FieldIndexer;
import com.google.appengine.api.search.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.google.appengine.api.search.Field.newBuilder;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
public class DateFieldIndexer implements FieldIndexer {

  @Override
  public List<Field> index(String fieldName, java.lang.reflect.Field field, Object fieldInstanceValue) {

    List<Field> fields = new ArrayList<Field>();
    Field documentField;

    Date fieldValue = (Date) fieldInstanceValue;
    if (fieldValue != null) {
      Long dateInSeconds = fieldValue.getTime() / 1000;
      documentField = newBuilder().setName(fieldName).setNumber(dateInSeconds).build();

    } else {
      documentField = newBuilder().setName(fieldName).setNumber(0).build();
    }

    fields.add(documentField);

    return fields;
  }
}

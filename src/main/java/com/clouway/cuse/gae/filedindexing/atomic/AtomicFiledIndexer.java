package com.clouway.cuse.gae.filedindexing.atomic;

import com.clouway.cuse.gae.filedindexing.FieldIndexer;
import com.google.appengine.api.search.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.clouway.cuse.spi.EscapeUtil.escape;
import static com.google.appengine.api.search.Field.newBuilder;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class AtomicFiledIndexer implements FieldIndexer {

  @Override
  public List<Field> index(String fieldName, java.lang.reflect.Field field, Object fieldInstanceValue) {

    List<Field> fields = new ArrayList<Field>();
    Set<String> fieldValues = new HashSet<String>();

    if (fieldInstanceValue == null) {
      fieldValues.add("");
    } else if (fieldInstanceValue instanceof Collection) {
      Collection collection = (Collection) fieldInstanceValue;
      for (Object object : collection) {
        fieldValues.add(String.valueOf(object));
      }
    } else {
      fieldValues.add(String.valueOf(fieldInstanceValue));
    }

    for (String fieldValue : fieldValues) {
      String escapedValue = escape(String.valueOf(fieldValue));
      Field newField = newBuilder().setName(fieldName).setAtom(escapedValue).build();
      fields.add(newField);
    }

    return fields;
  }
}
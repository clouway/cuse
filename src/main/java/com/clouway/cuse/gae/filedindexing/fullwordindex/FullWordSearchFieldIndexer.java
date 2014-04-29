package com.clouway.cuse.gae.filedindexing.fullwordindex;

import com.clouway.cuse.gae.filedindexing.FieldIndexer;
import com.google.appengine.api.search.Field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.appengine.api.search.Field.newBuilder;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
public class FullWordSearchFieldIndexer implements FieldIndexer {

  @Override
  public List<Field> index(String fieldName, java.lang.reflect.Field field, Object fieldInstanceValue) {

    List<Field> fields = new ArrayList<Field>();
    Set<String> fieldValues = new HashSet<String>();

    if (fieldInstanceValue == null) {
      fieldValues.add("");
    } else if (fieldInstanceValue instanceof Collection) {
      Collection collection = (Collection) fieldInstanceValue;
      for (Object object : collection) {
        fieldValues.addAll(split(String.valueOf(object)));
      }
    } else {
      fieldValues.addAll(split(String.valueOf(fieldInstanceValue)));
    }

    for (String fieldValue : fieldValues) {
      Field newField = newBuilder().setName(fieldName).setText(String.valueOf(fieldValue)).build();
      fields.add(newField);
    }

    return fields;
  }

  private Collection<? extends String> split(String value) {
    String[] splitValues = value.split(" ");
    return Arrays.asList(splitValues);
  }
}

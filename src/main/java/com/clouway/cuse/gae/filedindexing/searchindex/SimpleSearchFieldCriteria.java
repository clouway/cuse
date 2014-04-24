package com.clouway.cuse.gae.filedindexing.searchindex;

import com.clouway.cuse.gae.filedindexing.FieldCriteria;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
public class SimpleSearchFieldCriteria implements FieldCriteria {
  @Override
  public boolean match(Field field) {

    Annotation[] annotations = field.getAnnotations();
    if(!Date.class.isAssignableFrom(field.getType()) && (annotations == null || annotations.length == 0)){
      return true;
    }
    return false;
  }
}

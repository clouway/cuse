package com.clouway.cuse.gae.filedindexing.dateindex;

import com.clouway.cuse.gae.filedindexing.FieldCriteria;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
public class DateFieldCriteria implements FieldCriteria {
  @Override
  public boolean match(Field field) {

    return Date.class.isAssignableFrom(field.getType());
  }
}

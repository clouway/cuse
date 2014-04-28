package com.clouway.cuse.gae.filedindexing;

import java.lang.reflect.Field;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
public interface FieldCriteria {
  boolean match(Field field);

}

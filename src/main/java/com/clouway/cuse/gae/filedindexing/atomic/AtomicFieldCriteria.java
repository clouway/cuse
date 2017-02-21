package com.clouway.cuse.gae.filedindexing.atomic;

import com.clouway.cuse.gae.filedindexing.FieldCriteria;
import com.clouway.cuse.spi.annotations.Atomic;
import com.clouway.cuse.spi.annotations.SearchId;

import java.lang.reflect.Field;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class AtomicFieldCriteria implements FieldCriteria {
  @Override
  public boolean match(Field field) {

    Atomic annotation = field.getAnnotation(Atomic.class);
    if(annotation != null){
      return true;
    }
    return false;
  }
}

package com.clouway.cuse.gae.filedindexing.fullwordindex;

import com.clouway.cuse.gae.filedindexing.FieldCriteria;
import com.clouway.cuse.spi.annotations.FullWordSearch;

import java.lang.reflect.Field;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
public class FullWordSearchFieldCriteria implements FieldCriteria {
  @Override
  public boolean match(Field field) {

    FullWordSearch annotation = field.getAnnotation(FullWordSearch.class);
    if(annotation != null){
      return true;
    }
    return false;
  }
}

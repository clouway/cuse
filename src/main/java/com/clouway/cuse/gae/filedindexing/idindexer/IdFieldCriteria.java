package com.clouway.cuse.gae.filedindexing.idindexer;

import com.clouway.cuse.gae.filedindexing.FieldCriteria;
import com.clouway.cuse.spi.annotations.SearchId;

import java.lang.reflect.Field;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
public class IdFieldCriteria implements FieldCriteria {
  @Override
  public boolean match(Field field) {

    SearchId annotation = field.getAnnotation(SearchId.class);
    if(annotation != null){
      return true;
    }
    return false;
  }
}

package com.clouway.cuse.gae.filedindexing.fulltextindex;

import com.clouway.cuse.gae.filedindexing.FieldCriteria;
import com.clouway.cuse.spi.annotations.FullTextSearch;

import java.lang.reflect.Field;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
public class FullTextSearchFieldCriteria implements FieldCriteria {
  @Override
  public boolean match(Field field) {

    FullTextSearch annotation = field.getAnnotation(FullTextSearch.class);
    if(annotation != null){
      return true;
    }
    return false;
  }
}

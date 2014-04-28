package com.clouway.cuse.gae.filedindexing;


import com.google.appengine.api.search.Field;

import java.util.List;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
public interface FieldIndexer {

  List<Field> index(String fieldName, java.lang.reflect.Field field, Object fieldInstanceValue);

}

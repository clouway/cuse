package com.clouway.cuse.spi;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public interface SearchEngine {

  void register(Object object);

  <T> Search.SearchBuilder<T> search(Class<T> clazz);

  <T> Search.SearchBuilder<T> searchIds(Class<T> idClass);

  void deleteDocument(Class indexClass, List<Long> ids);
}

package com.clouway.searchengine;

import com.clouway.searchengine.spi.IndexingStrategy;
import com.clouway.searchengine.spi.IndexingStrategyCatalog;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class InMemoryIndexingStrategyCatalog implements IndexingStrategyCatalog {

  public IndexingStrategy get(Class aClass) {

    if (aClass.equals(Employee.class)) {
      return new EmployeeIndexingStrategy();
    }

    if (aClass.equals(User.class)) {
      return new UserIndexingStrategy();
    }

    return null;
  }
}

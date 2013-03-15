package com.clouway.searchengine;

import com.clouway.searchengine.spi.IdConvertor;
import com.clouway.searchengine.spi.IdConvertorCatalog;
import com.clouway.searchengine.spi.LongIdConvertor;
import com.clouway.searchengine.spi.StringIdConvertor;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class InMemoryIdConvertorCatalog implements IdConvertorCatalog {

  public IdConvertor getConvertor(Class aClass) {

    if (aClass.equals(String.class)) {
      return new StringIdConvertor();
    }

    if (aClass.equals(Long.class)) {
      return new LongIdConvertor();
    }
    return null;
  }
}

package com.clouway.cuse;

import com.clouway.cuse.spi.IdConvertor;
import com.clouway.cuse.spi.IdConvertorCatalog;
import com.clouway.cuse.spi.LongIdConvertor;
import com.clouway.cuse.spi.StringIdConvertor;

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

package com.clouway.cuse.spi;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class DefaultIdConvertorCatalog implements IdConvertorCatalog {

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

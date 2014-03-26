package com.clouway.cuse.spi;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class DefaultIdConverterCatalog implements IdConverterCatalog {

  public IdConverter getConverter(Class aClass) {

    if (aClass.equals(String.class)) {
      return new StringIdConverter();
    }

    if (aClass.equals(Long.class)) {
      return new LongIdConverter();
    }
    return null;
  }
}

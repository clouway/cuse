package com.clouway.cuse.spi;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public interface IdConverterCatalog {

  IdConverter getConverter(Class aClass);

}

package com.clouway.searchengine.spi;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public interface IdConvertorCatalog {

  IdConvertor getConvertor(Class aClass);
}

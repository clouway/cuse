package com.clouway.cuse.spi;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public interface IdConvertorCatalog {

  IdConvertor getConvertor(Class aClass);
}

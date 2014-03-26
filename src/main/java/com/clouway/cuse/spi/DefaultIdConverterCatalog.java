package com.clouway.cuse.spi;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class DefaultIdConverterCatalog implements IdConverterCatalog {

  private Map<Class, IdConverter> idConvertersMap = new HashMap<Class, IdConverter>(){{
    put(String.class, new StringIdConverter());
    put(Long.class, new LongIdConverter());
  }};

  @Override
  public IdConverter getConverter(Class aClass) {
    return idConvertersMap.get(aClass);
  }

  public void addIdConverter(Class aClass, IdConverter idConverter) {
    idConvertersMap.put(aClass, idConverter);
  }
}

package com.clouway.cuse.spi;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class IndexingStrategyCatalogImpl implements IndexingStrategyCatalog {


  private Map<Class<?>,IndexingStrategy<?>> map = new HashMap<Class<?>, IndexingStrategy<?>>();

  public IndexingStrategyCatalogImpl() {
  }

  public void put(Class<?> indexClazz, IndexingStrategy<?> strategyClazz){
    map.put(indexClazz,strategyClazz);
  }

  @Override
  public IndexingStrategy get(Class<?> indexClazz) {
    if(map.containsKey(indexClazz)){
      return map.get(indexClazz);
    }
    return null;
  }
}

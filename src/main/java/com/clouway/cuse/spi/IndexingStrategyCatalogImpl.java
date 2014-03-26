package com.clouway.cuse.spi;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class IndexingStrategyCatalogImpl implements IndexingStrategyCatalog {

  private IndexStrategyFactory indexStrategyFactory;

  private Map<Class<?>,IndexingStrategy<?>> map = new HashMap<Class<?>, IndexingStrategy<?>>();

  public IndexingStrategyCatalogImpl(IndexStrategyFactory indexStrategyFactory) {
    this.indexStrategyFactory = indexStrategyFactory;
  }

  public void put(Class<?> indexClazz, IndexingStrategy<?> strategyClazz){
    map.put(indexClazz,strategyClazz);
  }

  @Override
  public IndexingStrategy get(final Class<?> indexClazz) {
    if(map.containsKey(indexClazz)){
      return map.get(indexClazz);
    }else {

      IndexingStrategy indexingStrategy = indexStrategyFactory.create(indexClazz);
      if(indexingStrategy != null) {
        map.put(indexClazz, indexingStrategy);
        return indexingStrategy;
      }
    }
    return null;
  }
}

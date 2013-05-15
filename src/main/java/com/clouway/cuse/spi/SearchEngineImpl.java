package com.clouway.cuse.spi;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class SearchEngineImpl implements SearchEngine {

  private final EntityLoader entityLoader;
  private final IndexingStrategyCatalog indexingStrategyCatalog;
  private final IdConvertorCatalog idConvertorCatalog;
  private final IndexRegister indexRegister;
  private final MatchedIdObjectFinder objectIdFinder;

  @Inject
  public SearchEngineImpl(EntityLoader entityLoader, IndexingStrategyCatalog indexingStrategyCatalog, IdConvertorCatalog idConvertorCatalog,
                          IndexRegister indexRegister, MatchedIdObjectFinder objectIdFinder) {
    this.entityLoader = entityLoader;
    this.indexingStrategyCatalog = indexingStrategyCatalog;
    this.idConvertorCatalog = idConvertorCatalog;
    this.indexRegister = indexRegister;
    this.objectIdFinder = objectIdFinder;
  }

  public void register(Object instance) {

    Class instanceClass = instance.getClass();
    IndexingStrategy strategy = indexingStrategyCatalog.get(instanceClass);

    if (strategy == null) {
      throw new NotConfiguredIndexingStrategyException();
    }

    indexRegister.register(instance, strategy);

  }

  public <T> Search.SearchBuilder<T> search(Class<T> clazz) {
    return new Search.SearchBuilder<T>(clazz, entityLoader, indexingStrategyCatalog, objectIdFinder);
  }

  @Override
  public <T> Search.SearchBuilder<T> searchIds(Class<T> idClass) {

    if (idConvertorCatalog.getConvertor(idClass) == null) {
      throw new NotConfiguredIdConvertorException();
    }
    return new Search.SearchBuilder<T>(idClass, idClass, entityLoader, indexingStrategyCatalog, idConvertorCatalog, objectIdFinder);
  }

  @Override
  public void deleteDocument(Class indexClass, List<Long> ids) {
    List<String> stringIds = new ArrayList<String>();
    for (Long id : ids)
    {
      stringIds.add(String.valueOf(id));
    }
    indexRegister.loadIndex(indexClass.getSimpleName()).delete(stringIds);
  }

}

package com.clouway.cuse.spi;

import com.clouway.cuse.spi.annotations.SearchIndex;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
class SearchEngineImpl implements SearchEngine {

  private final EntityLoader entityLoader;
  private final IndexingStrategyCatalog indexingStrategyCatalog;
  private final IdConverterCatalog idConverterCatalog;
  private final IndexRegistry indexRegistry;
  private final MatchedIdObjectFinder objectIdFinder;


  public SearchEngineImpl(EntityLoader entityLoader,
                          IdConverterCatalog idConverterCatalog,
                          IndexingStrategyCatalog indexingStrategyCatalog,
                          IndexRegistry indexRegistry,
                          MatchedIdObjectFinder objectIdFinder) {
    this.entityLoader = entityLoader;
    this.idConverterCatalog = idConverterCatalog;
    this.indexingStrategyCatalog = indexingStrategyCatalog;
    this.indexRegistry = indexRegistry;
    this.objectIdFinder = objectIdFinder;
  }

  public void register(Object instance) {
    Class instanceClass = instance.getClass();
    IndexingStrategy strategy = indexingStrategyCatalog.get(instanceClass);
    if (strategy == null) {
      throw new NotConfiguredIndexingStrategyException();
    }
    indexRegistry.register(instance, strategy);
  }

  public void registerAll(List<? extends Object> objects) {
    if (objects.isEmpty()) {
      return;
    }
    if (isWithEquivalentSearchIndexes(objects)) {
      Class instanceClass = objects.get(0).getClass();
      IndexingStrategy strategy = indexingStrategyCatalog.get(instanceClass);
      if (strategy == null) {
        throw new NotConfiguredIndexingStrategyException();
      }
      indexRegistry.registerAll(objects, strategy);
    }
  }

  public <T> Search.SearchBuilder<T> search(Class<T> clazz) {
    return new Search.SearchBuilder<T>(clazz, entityLoader, indexingStrategyCatalog, objectIdFinder);
  }

  @Override
  public <T> Search.SearchBuilder<T> searchIds(Class<T> idClass) {

    if (idConverterCatalog.getConverter(idClass) == null) {
      throw new NotConfiguredIdConvertorException();
    }
    return new Search.SearchBuilder<T>(idClass, idClass, entityLoader, indexingStrategyCatalog, idConverterCatalog, objectIdFinder);
  }

  @Override
  public void delete(String indexName, List<Long> objectIds) {
    indexRegistry.delete(indexName, objectIds);
  }

  private boolean isWithEquivalentSearchIndexes(List<? extends Object> objects) {
    Annotation searchIndexToMatch = objects.get(0).getClass().getAnnotation(SearchIndex.class);
    if (searchIndexToMatch == null) {
      throw new SearchIndexMissingException();
    }
    for (Object object : objects) {
      Annotation searchIndex = object.getClass().getAnnotation(SearchIndex.class);
      if (searchIndex == null) {
        throw new SearchIndexMissingException();
      }
      if (!searchIndexToMatch.equals(searchIndex)) {
        throw new SearchIndexMissmatchException();
      }
    }
    return true;
  }
}

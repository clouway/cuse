package com.clouway.searchengine.spi;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class InMemorySearchEngine implements SearchEngine {
  @Override
  public void register(Object instance) {
//    Class instanceClass = instance.getClass();
//    IndexingStrategy strategy = indexingStrategyCatalog.get(instanceClass);
//
//    if (strategy == null) {
//      throw new NotConfiguredIndexingStrategyException();
//    }
//
//    String documentId = strategy.getId(instance);
//
//    IndexingSchema indexingSchema = strategy.getIndexingSchema();
//    List<String> fields = indexingSchema.getFields();
//    List<String> fullTextFields = indexingSchema.getFullText();
//
//    memory.register(fields, fullTextFields, instance);
  }

  @Override
  public <T> Search.SearchBuilder<T> search(Class<T> clazz) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public <T> Search.SearchBuilder<T> searchIds(Class<T> idClass) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}

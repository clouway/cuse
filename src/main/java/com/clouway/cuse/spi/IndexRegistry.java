package com.clouway.cuse.spi;


import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public interface IndexRegistry {

  void register(Object instance, IndexingStrategy strategy);

  void registerAll(List<? extends Object> instances,IndexingStrategy strategy);

  void delete(String indexName, List<Long> objectIds);
}

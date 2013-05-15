package com.clouway.cuse.spi;


import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public interface IndexRegister {
  void register(Object instance, IndexingStrategy strategy);

  void delete(String indexName, List<Long> objectIds);
}

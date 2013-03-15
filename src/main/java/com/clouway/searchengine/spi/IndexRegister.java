package com.clouway.searchengine.spi;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public interface IndexRegister {
  void register(Object instance, IndexingStrategy strategy);
}

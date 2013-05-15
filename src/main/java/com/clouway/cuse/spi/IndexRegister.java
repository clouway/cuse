package com.clouway.cuse.spi;

import com.google.appengine.api.search.Index;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public interface IndexRegister {
  void register(Object instance, IndexingStrategy strategy);

  Index loadIndex(String indexName);
}

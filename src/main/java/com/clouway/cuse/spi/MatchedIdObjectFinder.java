package com.clouway.cuse.spi;

import java.util.List;
import java.util.Map;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public interface MatchedIdObjectFinder {

  List<String> find(String indexName, Map<String, SearchFilter> filters, int limit);
}

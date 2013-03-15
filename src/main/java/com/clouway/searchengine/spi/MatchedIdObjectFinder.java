package com.clouway.searchengine.spi;

import java.util.List;
import java.util.Map;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public interface MatchedIdObjectFinder {

  List<String> find(String indexName, Map<String, SearchMatcher> filters, int limit);
}

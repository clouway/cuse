package com.clouway.cuse.spi;

import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public interface MatchedIdObjectFinder {

  List<String> find(String indexName, List<String> filters, int limit, int offset, String sortingField, SortOrder sortOrder);
}

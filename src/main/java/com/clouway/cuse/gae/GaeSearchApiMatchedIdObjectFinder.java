package com.clouway.cuse.gae;

import com.clouway.cuse.spi.MatchedIdObjectFinder;
import com.clouway.cuse.spi.NegativeSearchLimitException;
import com.clouway.cuse.spi.SearchLimitExceededException;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class GaeSearchApiMatchedIdObjectFinder implements MatchedIdObjectFinder {

  @Override
  public List<String> find(String indexName, List<String> filters, int limit, int offset) {

    String query = buildQueryFilter(filters);

    Results<ScoredDocument> results = SearchServiceFactory.getSearchService().getIndex(IndexSpec.newBuilder()
            .setName(indexName))
            .search(buildQuery(query, limit, offset));

    List<String> entityIds = new ArrayList<String>();
    for (ScoredDocument scoredDoc : results) {
      entityIds.add(scoredDoc.getId());
    }

    return entityIds;
  }

  private String buildQueryFilter(List<String> filters) {

    StringBuilder queryFilter = new StringBuilder();

    for (String filter : filters) {
      queryFilter.append(filter).append(" ");
    }

    return queryFilter.toString();
  }

  private Query buildQuery(String searchQuery, int limit, int offset) {

    QueryOptions queryOptions = buildQueryOptions(limit, offset);

    return Query.newBuilder().setOptions(queryOptions).build(searchQuery);
  }

  private QueryOptions buildQueryOptions(int limit, int offset) {

    QueryOptions.Builder queryOptionsBuilder = QueryOptions.newBuilder().setReturningIdsOnly(true);

    if (limit > 1000) {
      throw new SearchLimitExceededException();
    }

    if (limit < 0) {
      throw new NegativeSearchLimitException();
    }

    if (limit > 0) {
      queryOptionsBuilder.setLimit(limit);
    }

    queryOptionsBuilder.setOffset(offset);

    return queryOptionsBuilder.build();
  }
}

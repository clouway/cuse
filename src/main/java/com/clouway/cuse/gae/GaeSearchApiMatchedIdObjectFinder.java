package com.clouway.cuse.gae;

import com.clouway.cuse.gae.exceptions.InvalidIndexNameException;
import com.clouway.cuse.gae.exceptions.InvalidSearchQueryException;
import com.clouway.cuse.gae.exceptions.UnableToCreateSearchServiceException;
import com.clouway.cuse.gae.exceptions.UnableToLoadSearchIndexException;
import com.clouway.cuse.spi.MatchedIdObjectFinder;
import com.clouway.cuse.spi.NegativeSearchLimitException;
import com.clouway.cuse.spi.SearchLimitExceededException;
import com.google.appengine.api.search.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class GaeSearchApiMatchedIdObjectFinder implements MatchedIdObjectFinder {

  @Override
  public List<String> find(String indexName, List<String> filters, int limit, int offset) {

    String query = buildQueryFilter(filters);

    IndexSpec indexSpec;

    try {
      indexSpec = IndexSpec.newBuilder().setName(indexName).build();
    } catch (IllegalArgumentException e) {
      throw new InvalidIndexNameException();
    }

    SearchService searchService;

    try {
      searchService = SearchServiceFactory.getSearchService();
    } catch (IllegalArgumentException e) {
      throw new UnableToCreateSearchServiceException();
    }

    Index searchIndex;

    try {
      searchIndex = searchService.getIndex(indexSpec);
    } catch (GetException e) {
      throw new UnableToLoadSearchIndexException();
    }

    Results<ScoredDocument> documents;

    try {
      documents = searchIndex.search(buildQuery(query, limit, offset));
    } catch (IllegalArgumentException e) {
      throw new InvalidSearchQueryException();
    } catch (SearchQueryException e) {
      throw new InvalidSearchQueryException();
    } catch (SearchException e) {
      throw new InvalidSearchQueryException();
    }

    List<String> entityIds = new ArrayList<String>();
    for (ScoredDocument document : documents) {
      entityIds.add(document.getId());
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

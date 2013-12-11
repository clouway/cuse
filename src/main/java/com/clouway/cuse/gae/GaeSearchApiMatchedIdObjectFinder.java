package com.clouway.cuse.gae;

import com.clouway.cuse.spi.MatchedIdObjectFinder;
import com.clouway.cuse.spi.NegativeSearchLimitException;
import com.clouway.cuse.spi.SearchLimitExceededException;
import com.clouway.cuse.spi.SortOrder;
import com.google.appengine.api.search.*;
import com.google.appengine.repackaged.com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class GaeSearchApiMatchedIdObjectFinder implements MatchedIdObjectFinder {

  @Override
  public List<String> find(String indexName, List<String> filters, int limit, int offset, String sortingField, SortOrder sortOrder) {

    String query = buildSearchQuery(filters);

    Results<ScoredDocument> results = SearchServiceFactory.getSearchService().getIndex(IndexSpec.newBuilder()
            .setName(indexName))
            .search(buildQuery(query, limit, offset, sortingField, sortOrder));

    List<String> entityIds = new ArrayList<String>();
    for (ScoredDocument scoredDoc : results) {
      entityIds.add(scoredDoc.getId());
    }

    return entityIds;
  }

  private String buildSearchQuery(List<String> filters) {

    StringBuilder queryFilter = new StringBuilder();

    for (String filter : filters) {
      queryFilter.append(filter).append(" ");
    }

    return queryFilter.toString();
  }

  private Query buildQuery(String searchQuery, int limit, int offset, String sortingField, SortOrder sortOrder) {

    QueryOptions.Builder queryOptionsBuilder = queryOptionsBuilder(limit, offset);

    if (!Strings.isNullOrEmpty(sortingField)) {
      SortOptions sortOptions = buildSortOptions(sortingField, sortOrder);
      queryOptionsBuilder.setSortOptions(sortOptions);
    }

    QueryOptions queryOptions = queryOptionsBuilder.build();

    return Query.newBuilder().setOptions(queryOptions).build(searchQuery);
  }

  private QueryOptions.Builder queryOptionsBuilder(int limit, int offset) {

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

    return queryOptionsBuilder;
  }

  private SortOptions buildSortOptions(String sortingField, SortOrder sortOrder) {

    SortOptions.Builder sortOptionsBuilder = SortOptions.newBuilder();

    SortExpression.Builder sortExpressionBuilder = SortExpression.newBuilder();
    sortExpressionBuilder.setDefaultValue("id");
    sortExpressionBuilder.setExpression(sortingField);

    if (sortOrder.equals(SortOrder.ASCENDING)) {
      sortExpressionBuilder.setDirection(SortExpression.SortDirection.ASCENDING);
    }

    if (sortOrder.equals(SortOrder.DESCENDING)) {
      sortExpressionBuilder.setDirection(SortExpression.SortDirection.DESCENDING);
    }

    SortExpression sortExpression = sortExpressionBuilder.build();

    sortOptionsBuilder.addSortExpression(sortExpression);

    return sortOptionsBuilder.build();
  }
}

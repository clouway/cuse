package com.clouway.searchengine;

import com.google.appengine.api.search.Consistency;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class Search<T> {

  public static final class SearchBuilder<T> {

    private Class clazz;
    private EntityLoader entityLoader;
    private IndexingStrategyCatalog indexingStrategyCatalog;
    private Map<String, SearchMatcher> filters = new HashMap<String, SearchMatcher>();
    private String query = "";
    private String index;

    public SearchBuilder(Class clazz, EntityLoader entityLoader, IndexingStrategyCatalog indexingStrategyCatalog) {
      this.clazz = clazz;
      this.entityLoader = entityLoader;
      this.indexingStrategyCatalog = indexingStrategyCatalog;
    }

    public SearchBuilder<T> where(String field, SearchMatcher matcher) {
      filters.put(field, matcher);
      return this;
    }

    public SearchBuilder<T> where(SearchMatcher matcher) {
      this.query = matcher.getValue();
      return this;
    }

    public SearchBuilder<T> inIndex(Class aClass) {
      this.index = aClass.getSimpleName();
      return this;
    }

    public Search<T> returnAll() {

      Search<T> search = new Search<T>( );
      search.clazz = clazz;
      search.filters = filters;
      search.entityLoader = entityLoader;
      search.indexingStrategyCatalog = indexingStrategyCatalog;
      search.query = query;
      search.index = index;

      return search;
    }
  }

  private Class<T> clazz;
  private Map<String, SearchMatcher> filters;
  private EntityLoader entityLoader;
  private IndexingStrategyCatalog indexingStrategyCatalog;
  private String query;
  private String index;
  private int limit;

  private Search() {
  }

  public List<T> now() {

    StringBuilder queryBuilder = new StringBuilder();

    for (String filter : filters.keySet()) {
      StringBuilder filterBuilder = new StringBuilder();
      filterBuilder.append(filter).append(":").append(filters.get(filter).getValue());
      queryBuilder.append(filterBuilder).append(" ");
    }

    String searchQuery = this.query + queryBuilder.toString();



    QueryOptions.Builder queryOptionsBuilder = QueryOptions.newBuilder().setReturningIdsOnly(true);

    if (limit > 0) {
      queryOptionsBuilder.setLimit(limit);
    }

    Query query = Query.newBuilder().setOptions(queryOptionsBuilder.build()).build(searchQuery);

    String indexName;

    if (index != null && index.length() > 0) {
      indexName = index;
    } else {
      indexName = indexingStrategyCatalog.get(clazz).getIndexName();
    }

    Results<ScoredDocument> results = SearchServiceFactory.getSearchService().getIndex(IndexSpec.newBuilder()
                                                                             .setName(indexName)
                                                                             .setConsistency(Consistency.PER_DOCUMENT))
                                                                             .search(query);

    List<String> entityIds = new ArrayList<String>();
    for (ScoredDocument scoredDoc : results) {
      entityIds.add(scoredDoc.getId());
    }

    return entityLoader.loadAll(clazz, entityIds);
  }

  public Search<T> limit(int limit) {
    this.limit = limit;
    return this;
  }
}

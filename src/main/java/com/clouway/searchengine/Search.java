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

    public SearchBuilder(Class clazz, EntityLoader entityLoader, IndexingStrategyCatalog indexingStrategyCatalog) {
      this.clazz = clazz;
      this.entityLoader = entityLoader;
      this.indexingStrategyCatalog = indexingStrategyCatalog;
    }

    public SearchBuilder<T> where(String field, SearchMatcher matcher) {
      filters.put(field, matcher);
      return this;
    }

    public Search<T> returnAll() {

      Search<T> search = new Search<T>( );
      search.clazz = clazz;
      search.filters = filters;
      search.entityLoader = entityLoader;
      search.indexingStrategyCatalog = indexingStrategyCatalog;

      return search;
    }
  }

  private Class<T> clazz;
  private Map<String, SearchMatcher> filters;
  private EntityLoader entityLoader;
  private IndexingStrategyCatalog indexingStrategyCatalog;

  private Search() {
  }

  public List<T> now() {

    String queryStr = "";

    StringBuilder paramsQuery = new StringBuilder();

    for (String fieldName : filters.keySet()) {
      StringBuilder builder = new StringBuilder();
      builder.append(fieldName).append(":").append(filters.get(fieldName).getValue());
      paramsQuery.append(builder).append(" ");
    }

    queryStr = paramsQuery.append(queryStr).toString();

    QueryOptions.Builder optionsBuilder = QueryOptions.newBuilder().setReturningIdsOnly(true);

    Query query = Query.newBuilder().setOptions(optionsBuilder.build()).build(queryStr);

    String indexName = indexingStrategyCatalog.get(clazz).getIndexName();
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
}

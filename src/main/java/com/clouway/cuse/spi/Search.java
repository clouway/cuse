package com.clouway.cuse.spi;

import com.clouway.cuse.spi.filters.SearchFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class Search<T> {

  public static final class SearchBuilder<T> {

    private Class<T> clazz;
    private Class<T> idClazz;

    private EntityLoader entityLoader;
    private IndexingStrategyCatalog indexingStrategyCatalog;
    private IdConvertorCatalog idConvertorCatalog;
    private final MatchedIdObjectFinder objectIdFinder;

    private final List<String> filters = new ArrayList<String>();
    private String index;
    private int offset;

    public SearchBuilder(Class<T> clazz, EntityLoader entityLoader, IndexingStrategyCatalog indexingStrategyCatalog, MatchedIdObjectFinder objectIdFinder) {
      this.clazz = clazz;
      this.entityLoader = entityLoader;
      this.indexingStrategyCatalog = indexingStrategyCatalog;
      this.objectIdFinder = objectIdFinder;
    }

    public SearchBuilder(Class<T> clazz, Class<T> idClazz, EntityLoader entityLoader, IndexingStrategyCatalog indexingStrategyCatalog, IdConvertorCatalog idConvertorCatalog,
                         MatchedIdObjectFinder objectIdFinder) {
      this.clazz = clazz;
      this.idClazz = idClazz;
      this.entityLoader = entityLoader;
      this.indexingStrategyCatalog = indexingStrategyCatalog;
      this.idConvertorCatalog = idConvertorCatalog;
      this.objectIdFinder = objectIdFinder;
    }

    public SearchBuilder<T> where(String field, SearchFilter filter) {
      filters.add(filter.getValue(Arrays.asList(field)));
      return this;
    }

    public SearchBuilder where(List<String> fields, SearchFilter filter) {
      filters.add(filter.getValue(fields));
      return this;
    }

    public SearchBuilder<T> where(final String query) {

      if (query == null || "".equals(query.trim())) {
        throw new EmptySearchQueryException();
      }
      filters.add(query);

      return this;
    }

    public SearchBuilder<T> inIndex(Class aClass) {
      this.index = aClass.getSimpleName();
      return this;
    }

    public SearchBuilder<T> offset(int offset) {
      this.offset = offset;
      return this;
    }

    public Search<T> fetchMaximum(int limit) {

      Search<T> search = returnAll();
      search.limit = limit;

      return search;
    }

    public Search<T> returnAll() {

      Search<T> search = new Search<T>();

      search.clazz = clazz;
      search.idClazz = idClazz;

      search.entityLoader = entityLoader;
      search.indexingStrategyCatalog = indexingStrategyCatalog;
      search.idConvertorCatalog = idConvertorCatalog;

      search.filters = filters;
      search.index = index;

      search.objectIdFinder = objectIdFinder;
      search.limit = 1000;
      search.offset = offset;

      return search;
    }
  }

  private Class<T> clazz;
  private Class<T> idClazz;

  private EntityLoader entityLoader;
  private IndexingStrategyCatalog indexingStrategyCatalog;
  private IdConvertorCatalog idConvertorCatalog;

  private List<String> filters;
  private String index;
  private int limit;
  private int offset;

  private MatchedIdObjectFinder objectIdFinder;

  private Search() {
  }

  public List<T> now() {

    if (filters == null || filters.size() == 0) {
      throw new MissingSearchFiltersException();
    }

    List<String> results = objectIdFinder.find(buildIndexName(index), filters, limit, offset);

    if (idClazz != null) {

      IdConvertor convertor = idConvertorCatalog.getConvertor(idClazz);

      if (convertor == null) {
        throw new NotConfiguredIdConvertorException();
      }

      return convertor.convert(results);
    }

    return entityLoader.loadAll(clazz, results);
  }

  private String buildIndexName(String index) {

    String indexName;

    if (index != null && index.length() > 0) {
      indexName = this.index;
    } else {

      if (indexingStrategyCatalog.get(clazz) == null) {
        throw new NotConfiguredIndexingStrategyException();
      }

      indexName = indexingStrategyCatalog.get(clazz).getIndexName();
    }

    return indexName;
  }
}

package com.clouway.cuse.spi;

import com.clouway.cuse.gae.GaeSearchApiIndexRegister;
import com.clouway.cuse.gae.GaeSearchApiMatchedIdObjectFinder;
import com.clouway.cuse.gae.filedindexing.FieldCriteria;
import com.clouway.cuse.gae.filedindexing.FieldIndexer;
import com.clouway.cuse.gae.filedindexing.dateindex.DateFieldCriteria;
import com.clouway.cuse.gae.filedindexing.dateindex.DateFieldIndexer;
import com.clouway.cuse.gae.filedindexing.fulltextindex.FullTextSearchFieldCriteria;
import com.clouway.cuse.gae.filedindexing.fulltextindex.FullTextSearchFieldIndexer;
import com.clouway.cuse.gae.filedindexing.fullwordindex.FullWordSearchFieldCriteria;
import com.clouway.cuse.gae.filedindexing.fullwordindex.FullWordSearchFieldIndexer;
import com.clouway.cuse.gae.filedindexing.idindexer.IdFieldCriteria;
import com.clouway.cuse.gae.filedindexing.idindexer.IdFieldIndexer;
import com.clouway.cuse.gae.filedindexing.searchindex.SimpleSearchFieldCriteria;
import com.clouway.cuse.gae.filedindexing.searchindex.SimpleSearchFieldIndexer;
import com.google.inject.util.Providers;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used for creation of instance on {@link com.clouway.cuse.spi.SearchEngine}
 *
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
public class SearchEngineFactory {

  /**
   * Create SearchEngine with given EntityLoader
   *
   * @param entityLoader which will be used from SearchEngine
   * @return SearchEngine
   */
  public static SearchEngine create(EntityLoader entityLoader) {
    return create(entityLoader, new DefaultIdConverterCatalog());
  }

  /**
   * Create SearchEngine with given EntityLoader and IdConverterCatalog
   *
   * @param entityLoader       which will be used from SearchEngine
   * @param idConverterCatalog which will be used from SearchEngine
   * @return SearchEngines
   */
  public static SearchEngine create(EntityLoader entityLoader, IdConverterCatalog idConverterCatalog) {
    IndexingStrategyCatalogImpl indexingStrategyCatalog = new IndexingStrategyCatalogImpl(new AnnotatedIndexStrategyFactory(
            Providers.of(idConverterCatalog)));

    Map<FieldCriteria, FieldIndexer> actionCriterias = new HashMap<FieldCriteria, FieldIndexer>(){{
      put(new SimpleSearchFieldCriteria(), new SimpleSearchFieldIndexer());
      put(new FullTextSearchFieldCriteria(), new FullTextSearchFieldIndexer());
      put(new DateFieldCriteria(), new DateFieldIndexer());
      put(new FullWordSearchFieldCriteria(), new FullWordSearchFieldIndexer());
      put(new IdFieldCriteria(), new IdFieldIndexer());
    }};


    GaeSearchApiIndexRegister indexRegister = new GaeSearchApiIndexRegister(actionCriterias);
    GaeSearchApiMatchedIdObjectFinder objectIdFinder = new GaeSearchApiMatchedIdObjectFinder();

    return new SearchEngineImpl(entityLoader, idConverterCatalog, indexingStrategyCatalog, indexRegister, objectIdFinder);
  }
}

package com.clouway.searchengine.gae;

import com.clouway.searchengine.SearchEngineContractTest;
import com.clouway.searchengine.spi.IndexRegister;
import com.clouway.searchengine.spi.MatchedIdObjectFinder;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class GaeSearchEngineTest extends SearchEngineContractTest {

  @Override
  public IndexRegister createIndexRegister() {
    return new GaeSearchApiIndexRegister();
  }

  @Override
  public MatchedIdObjectFinder createObjectIdFinder() {
    return new GaeSearchApiMatchedIdObjectFinder();
  }
}

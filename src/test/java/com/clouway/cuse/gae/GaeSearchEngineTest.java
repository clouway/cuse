package com.clouway.cuse.gae;

import com.clouway.cuse.SearchEngineContractTest;
import com.clouway.cuse.spi.IndexRegister;
import com.clouway.cuse.spi.MatchedIdObjectFinder;

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

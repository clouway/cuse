package com.clouway.cuse.gae;

import com.clouway.cuse.gae.filedindexing.FieldCriteria;
import com.clouway.cuse.gae.filedindexing.FieldIndexer;
import com.clouway.cuse.spi.AddressIndex;
import com.clouway.cuse.spi.IndexCreationFailureException;
import com.clouway.cuse.spi.IndexingStrategy;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.OperationResult;
import com.google.appengine.api.search.PutResponse;
import com.google.appengine.api.search.SearchService;
import com.google.appengine.api.search.StatusCode;
import com.google.common.collect.Lists;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class RegisterNewIndexesTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  IndexingStrategy<AddressIndex> strategy;

  @Mock
  Index index;

  @Mock
  SearchService searchService;

  @Test
  public void happyPath() {
    checkIndexingThatWillReturn(responseWithStatus(StatusCode.OK));
  }

  @Test(expected = IndexCreationFailureException.class)
  public void failsWithTimeout() {
    checkIndexingThatWillReturn(responseWithStatus(StatusCode.TIMEOUT_ERROR));
  }

  @Test(expected = IndexCreationFailureException.class)
  public void failsWithConcurrentTransaction() {
    checkIndexingThatWillReturn(responseWithStatus(StatusCode.CONCURRENT_TRANSACTION_ERROR));
  }

  @Test(expected = IndexCreationFailureException.class)
  public void failsWithInternalError() {
    checkIndexingThatWillReturn(responseWithStatus(StatusCode.INTERNAL_ERROR));
  }

  private void checkIndexingThatWillReturn(final PutResponse response) {
    GaeIndexRegistry registry = new GaeIndexRegistry(new HashMap<FieldCriteria, FieldIndexer>(), searchService);

    final AddressIndex addressIndex = new AddressIndex(1L, "N/A", "1000");

    context.checking(new Expectations() {{
      oneOf(strategy).getId(addressIndex);
      will(returnValue("id1"));

      oneOf(strategy).getIndexName();
      will(returnValue("AddressIndex"));

      oneOf(searchService).getIndex(with(any(IndexSpec.Builder.class)));
      will(returnValue(index));

      oneOf(index).put(with(Expectations.<Document>anything()));
      will(returnValue(response));

    }});

    registry.register(addressIndex, strategy);
  }

  private PutResponse responseWithStatus(final StatusCode statusCode) {
    return new PutResponse(Lists.newArrayList(
            new OperationResult(statusCode, "failure")),
            new ArrayList<String>()) {{
    }};
  }

}
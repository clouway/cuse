package com.clouway.cuse.gae.filters;

import com.clouway.cuse.spi.filters.SearchFilter;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertThat;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
public class SearchFiltersTest{

  @Test
  public void escapeSymbolsForString() throws Exception {
    SearchFilter filter = SearchFilters.is(":");
    assertEscapedValue(filter, "field:\\: ", "field");
  }

  @Test
  public void escapeSymbolsForAnyString() throws Exception {
    SearchFilter filter = SearchFilters.anyIs(":");
    assertEscapedValue(filter, "fieldA:\\: OR fieldB:\\:", "fieldA", "fieldB");
  }

  @Test
  public void escapeSymbolsForAnyStringFromList() throws Exception {
    SearchFilter filter = SearchFilters.is(Arrays.asList(":"));
    assertEscapedValue(filter, "field:\\: ", "field");
  }

  @Test
  public void escapeSymbolsForAnyElementFromArray() throws Exception {
    SearchFilter filter = SearchFilters.isAnyOf(": =");
    assertEscapedValue(filter, "field:(\\: \\=)", "field");
  }

  @Test
  public void wrapWordsWithSpecialSymbols() throws Exception {
    SearchFilter filter = SearchFilters.isAnyOf("city 12:23:cd");
    assertEscapedValue(filter, "field:(city 12\\:23\\:cd)", "field");
  }

  private void assertEscapedValue(SearchFilter filter, String expectedValue, String... fields) {
    String value = filter.getValue(Arrays.asList(fields));
    assertThat("symbol is not escaped", value, Is.is(expectedValue));
  }
}

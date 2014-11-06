package com.clouway.cuse.gae.filters;

import com.clouway.cuse.spi.EmptySearchFilterException;
import com.clouway.cuse.spi.filters.SearchFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class SearchFilters {

  private static Map<String, String> escapeSymbolsMap = new HashMap<String, String>() {{
    put(":", "\\:");
    put(",", "\\,");
    put("+", "\\+");
    put("-", "\\\\-");
    put("=", "\\=");
    put("<", "\\<");
    put(">", "\\>");
  }};

  public static SearchFilter is(String value) {
    value = escapeSymbols(value);
    if (value == null || "".equals(value)) {
      throw new EmptySearchFilterException();
    }
    return new EqualitySearchFilter(value);
  }

  public static SearchFilter is(Boolean value) {
    return new EqualitySearchFilter(String.valueOf(value));
  }

  public static SearchFilter is(Integer value) {
    return new EqualitySearchFilter(String.valueOf(value));
  }

  public static SearchFilter is(Long value) {
    return new EqualitySearchFilter(String.valueOf(value));
  }

  public static SearchFilter isAnyOf(String... values) {
    return new OrSearchFilter(escapeSymbolsList(Arrays.asList(values)));
  }

  public static <T> SearchFilter isAnyOf(List<T> values) {
    if (values == null || values.size() == 0) {
      throw new EmptySearchFilterException();
    }
    return new OrSearchFilter(values);
  }

  public static SearchFilter anyIs(String value) {
    return new MultiFieldValueFilter(escapeSymbols(value));
  }

  public static SearchFilter lessThan(Date value) {
    return new LessThanFilter(new SearchableDate(value));
  }

  public static SearchFilter greaterThan(Date value) {
    return new GreaterThanFilter(new SearchableDate(value));
  }

  public static SearchFilter equalTo(Date value) {
    return new EqualitySearchFilter(new SearchableDate(value));
  }

  public static SearchFilter lessThanOrEqualTo(Date value) {
    return new LessThanOrEqualToFilter(new SearchableDate(value));
  }

  public static SearchFilter greaterThanOrEqualTo(Date value) {
    return new GreaterThanOrEqualToFilter(new SearchableDate(value));
  }

  public static SearchFilter is(List<String> values) {
    return new EqualitySearchFilter(escapeSymbolsList(values));
  }

  private static List<String> escapeSymbolsList(List<String> values) {
    List<String> valuesList = new ArrayList<String>();
    if (values != null) {
      for (String value : values) {
        valuesList.add(escapeSymbols(value));
      }
    }
    return valuesList;
  }

  private static String escapeSymbols(String value) {
    if (value == null || "".equals(value.trim())) {
      return "";
    }

    value = value.trim();

    for (String symbol : escapeSymbolsMap.keySet()) {
      value = value.replace(symbol, escapeSymbolsMap.get(symbol));
    }

    int countOfQuotes = value.length() - value.replaceAll("\"", "").length();

    //when quotes are odd count they should be ignored
    if (countOfQuotes % 2 != 0) {
      value = value.replace("\"", "");
    }

    return value;
  }
}

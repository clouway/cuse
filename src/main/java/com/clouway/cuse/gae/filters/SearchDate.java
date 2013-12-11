package com.clouway.cuse.gae.filters;

import java.util.Date;

/**
 * SearchDate class is used in order to create search queries by using {@link java.util.Date}s.
 * It's responsible for formatting the passed date in the appropriate format that the SearchAPI accepts.
 *
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
class SearchDate {

  private final Date date;

  public SearchDate(Date date) {
    this.date = date;
  }

  public String getValue() {
    return String.valueOf(date.getTime() / 1000);
  }
}

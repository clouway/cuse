package com.clouway.cuse.gae.filters;

import java.util.Date;

/**
 * SearchableDate class is used to transform the passed date into seconds.
 * This is done, because the SearchAPI strips out the time from the date.
 * In order to execute search queries which includes the time in the date,
 * we transform the the date in seconds and store it in the SearchAPI document as Number field.
 *
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
class SearchableDate {

  private final Date date;

  public SearchableDate(Date date) {
    this.date = date;
  }

  public String getValue() {
    return String.valueOf(date.getTime() / 1000);
  }
}

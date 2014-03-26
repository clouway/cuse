package com.clouway.cuse;

import com.clouway.cuse.spi.annotations.FullTextSearch;
import com.clouway.cuse.spi.annotations.FullWordSearch;
import com.clouway.cuse.spi.annotations.SearchId;
import com.clouway.cuse.spi.annotations.SearchIndex;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
@SearchIndex(name = "TicketIndex")
public class Ticket {

  @SearchId
  private Long id;

  @FullWordSearch
  private String title;

  @FullTextSearch
  private String description;

  public Ticket(Long id, String title, String description) {
    this.id = id;
    this.title = title;
    this.description = description;
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }
}

package com.clouway.cuse;

import com.clouway.cuse.spi.annotations.FullTextSearch;
import com.clouway.cuse.spi.annotations.FullWordSearch;
import com.clouway.cuse.spi.annotations.Ignore;
import com.clouway.cuse.spi.annotations.SearchId;
import com.clouway.cuse.spi.annotations.SearchIndex;

import java.util.Date;

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

  private Date creationDate;

  private String comment;

  @Ignore
  private String details;

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

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public void setDetails(String details) {
    this.details = details;
  }
}

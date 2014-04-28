package com.clouway.cuse.spi;

import com.clouway.cuse.spi.annotations.SearchId;
import com.clouway.cuse.spi.annotations.SearchIndex;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
@SearchIndex(name = "CompanyIndex")
public class CompanyIndex {

  @SearchId
  private Long entityId;

  private String name;

  private CustomerIndex customer;

  public CompanyIndex(Long entityId, String name, CustomerIndex customer) {
    this.entityId = entityId;
    this.name = name;
    this.customer = customer;
  }
}

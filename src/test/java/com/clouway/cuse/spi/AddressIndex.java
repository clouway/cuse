package com.clouway.cuse.spi;

import com.clouway.cuse.spi.annotations.FullTextSearch;
import com.clouway.cuse.spi.annotations.SearchId;
import com.clouway.cuse.spi.annotations.SearchIndex;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
@SearchIndex(name = "AddressIndex")
public class AddressIndex {

  @SearchId
  private Long entityId;

  @FullTextSearch
  private String city;

  private String postCode;

  public AddressIndex(Long entityId) {
    this.entityId = entityId;
  }

  public AddressIndex(Long entityId, String city, String postCode) {
    this.entityId = entityId;
    this.city = city;
    this.postCode = postCode;
  }
}

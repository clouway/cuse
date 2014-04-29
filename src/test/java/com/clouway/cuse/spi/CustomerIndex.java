package com.clouway.cuse.spi;

import com.clouway.cuse.spi.annotations.SearchId;
import com.clouway.cuse.spi.annotations.SearchIndex;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
@SearchIndex(name = "CustomerIndex")
public class CustomerIndex {

  @SearchId
  private Long entityId;

  private String customerName;

  private AddressIndex address;

  public CustomerIndex(Long entityId, AddressIndex address) {
    this.entityId = entityId;
    this.address = address;
  }

  public CustomerIndex(Long entityId, String customerName) {
    this.entityId = entityId;
    this.customerName = customerName;
  }

  public Long getEntityId() {
    return entityId;
  }
}

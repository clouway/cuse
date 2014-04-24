package com.clouway.cuse.spi;

import com.clouway.cuse.spi.annotations.SearchId;
import com.clouway.cuse.spi.annotations.SearchIndex;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
@SearchIndex(name = "EmployeeIndex")
public class EmployeeIndex {

  @SearchId
  private Long entityId;

  private String employeeName;

  private AddressIndex oldAddress;

  private AddressIndex newAddress;

  public EmployeeIndex(Long entityId, String employeeName, AddressIndex oldAddress, AddressIndex newAddress) {
    this.entityId = entityId;
    this.employeeName = employeeName;
    this.oldAddress = oldAddress;
    this.newAddress = newAddress;
  }
}

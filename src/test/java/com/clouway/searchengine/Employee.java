package com.clouway.searchengine;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class Employee {

  public Long id;
  public String firstName;
  public String lastName;

  public Employee(Long id, String firstName) {
    this.id = id;
    this.firstName = firstName;
  }

  public Employee(Long id, String firstName, String lastName) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
  }
}

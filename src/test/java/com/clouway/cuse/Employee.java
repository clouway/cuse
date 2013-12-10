package com.clouway.cuse;

import java.util.Date;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class Employee {

  static Builder aNewEmployee() {
    return new Builder();
  }

  static class Builder {

    private Long id;
    private Date birthDate;

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    public Builder birthDate(Date birthDate) {
      this.birthDate = birthDate;
      return this;
    }

    public Employee build() {
      return new Employee(id, birthDate);
    }
  }

  public Long id;
  public String firstName;
  public String lastName;
  public boolean assigned;
  public Date birthDate;

  public Employee(Long id, String firstName) {
    this.id = id;
    this.firstName = firstName;
  }

  public Employee(Long id, String firstName, String lastName) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public Employee(Long id, boolean assigned) {
    this.id = id;
    this.assigned = assigned;
  }

  public Employee(Long id, Date birthDate) {
    this.id = id;
    this.birthDate = birthDate;
  }
}

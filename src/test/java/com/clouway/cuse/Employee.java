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
    private String firstName;
    private Integer age;

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    public Builder birthDate(Date birthDate) {
      this.birthDate = birthDate;
      return this;
    }

    public Builder firstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    public Employee build() {

      Employee employee = new Employee();

      employee.id = id;
      employee.firstName = firstName;
      employee.age = age;
      employee.birthDate = birthDate;

      return employee;
    }

    public Builder age(Integer age) {
      this.age = age;
      return this;
    }
  }

  public Long id;
  public String firstName;
  public String lastName;
  public boolean assigned;
  public Date birthDate;
  private Integer age;

  public Employee() {
  }

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
}

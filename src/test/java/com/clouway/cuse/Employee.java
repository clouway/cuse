package com.clouway.cuse;

import com.clouway.cuse.spi.annotations.FullTextSearch;
import com.clouway.cuse.spi.annotations.SearchId;
import com.clouway.cuse.spi.annotations.SearchIndex;

import java.util.Date;
import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
@SearchIndex(name = "EmployeeIndex")
public class Employee {

  static Builder aNewEmployee() {
    return new Builder();
  }

  static class Builder {

    private Long id;
    private Date birthDate;
    private String firstName;
    private Integer age;
    private List<String> tags;

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
      employee.tags = tags;

      return employee;
    }

    public Builder age(Integer age) {
      this.age = age;
      return this;
    }

    public Builder tags(List<String> tags) {
      this.tags = tags;
      return this;
    }
  }

  @SearchId
  public Long id;
  public String firstName;

  @FullTextSearch
  public String lastName;
  public boolean assigned;
  public Date birthDate;
  private Integer age;
  private List<String> tags;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Employee employee = (Employee) o;

    if (assigned != employee.assigned) return false;
    if (age != null ? !age.equals(employee.age) : employee.age != null) return false;
    if (birthDate != null ? !birthDate.equals(employee.birthDate) : employee.birthDate != null) return false;
    if (firstName != null ? !firstName.equals(employee.firstName) : employee.firstName != null) return false;
    if (id != null ? !id.equals(employee.id) : employee.id != null) return false;
    if (lastName != null ? !lastName.equals(employee.lastName) : employee.lastName != null) return false;
    if (tags != null ? !tags.equals(employee.tags) : employee.tags != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
    result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
    result = 31 * result + (assigned ? 1 : 0);
    result = 31 * result + (birthDate != null ? birthDate.hashCode() : 0);
    result = 31 * result + (age != null ? age.hashCode() : 0);
    result = 31 * result + (tags != null ? tags.hashCode() : 0);
    return result;
  }
}

package com.clouway.searchengine;

import java.util.Arrays;
import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class EmployeeIndexingStrategy implements IndexingStrategy<Employee> {

  @Override
  public String getIndexName() {
    return Employee.class.getSimpleName();
  }

  @Override
  public List<String> getFields() {
    return Arrays.asList("firstName", "lastName");
  }

  @Override
  public String getId(Employee employee) {
    return String.valueOf(employee.id);
  }
}

package com.clouway.cuse;

import com.clouway.cuse.spi.IndexingSchema;
import com.clouway.cuse.spi.IndexingStrategy;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class EmployeeIndexingStrategy implements IndexingStrategy<Employee> {

  @Override
  public String getIndexName() {
    return Employee.class.getSimpleName();
  }

  @Override
  public String getId(Employee employee) {
    return String.valueOf(employee.id);
  }

  @Override
  public IndexingSchema getIndexingSchema() {
    return new IndexingSchema.IndexingSchemaBuilder().fields("firstName", "assigned", "birthDate", "age", "tags")
                                                     .fullTextFields("lastName")
                                                     .build();
  }
}

package com.clouway.cuse;

import com.clouway.cuse.spi.IndexingSchema;
import com.clouway.cuse.spi.IndexingStrategy;

import static com.clouway.cuse.spi.IndexingSchema.aNewIndexingSchema;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class UserIndexingStrategy implements IndexingStrategy<User> {

  public String getIndexName() {
    return User.class.getSimpleName();
  }

  @Override
  public String getId(User user) {
    return user.id.toString();
  }

  @Override
  public IndexingSchema getIndexingSchema() {
    return aNewIndexingSchema()
            .fields("family")
            .fullWordFields("description")
            .fullTextFields("name")
            .build();
  }
}

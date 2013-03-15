package com.clouway.cuse;

import com.clouway.cuse.spi.IndexingSchema;
import com.clouway.cuse.spi.IndexingStrategy;

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
    return IndexingSchema.aNewIndexingSchema().fullTextFields("name").build();
  }
}

package com.clouway.searchengine;

import java.util.Arrays;
import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class UserIndexingStrategy implements IndexingStrategy<User> {

  public String getIndexName() {
    return User.class.getSimpleName();
  }

  @Override
  public List<String> getFields() {
    return Arrays.asList("name");
  }

  @Override
  public String getId(User user) {
    return user.id.toString();
  }
}

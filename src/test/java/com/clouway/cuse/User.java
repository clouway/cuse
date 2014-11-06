package com.clouway.cuse;

import com.clouway.cuse.spi.annotations.FullTextSearch;
import com.clouway.cuse.spi.annotations.FullWordSearch;
import com.clouway.cuse.spi.annotations.SearchId;
import com.clouway.cuse.spi.annotations.SearchIndex;

import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
@SearchIndex(name = "UserIndex")
public class User {

  @SearchId
  public Long id;

  @FullTextSearch
  public String name;

  private String family;

  @FullWordSearch
  private String description;

  @FullTextSearch
  public List<String> tags;

  public User(Long id) {
    this.id = id;
  }

  public User(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public User(Long id, String name, String family) {
    this.id = id;
    this.name = name;
    this.family = family;
  }

  public User(Long id, String name, String family, String description) {
    this.id = id;
    this.name = name;
    this.family = family;
    this.description = description;
  }

  public User(Long id, List<String> tags) {
    this.id = id;
    this.tags = tags;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    User user = (User) o;

    if (description != null ? !description.equals(user.description) : user.description != null) return false;
    if (family != null ? !family.equals(user.family) : user.family != null) return false;
    if (id != null ? !id.equals(user.id) : user.id != null) return false;
    if (name != null ? !name.equals(user.name) : user.name != null) return false;
    if (tags != null ? !tags.equals(user.tags) : user.tags != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (family != null ? family.hashCode() : 0);
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + (tags != null ? tags.hashCode() : 0);
    return result;
  }
}

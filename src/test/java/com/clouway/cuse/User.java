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
}

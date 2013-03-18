package com.clouway.cuse;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class User {

  public Long id;
  public String name;
  private String family;

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
}

package com.clouway.searchengine;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public abstract class EntityLoaderContractTest {

  @Test
  public void loadEmptyList() {

    EntityLoader entityLoader = createEntityLoader();

    List<Entity> result = entityLoader.loadAll(Entity.class, new ArrayList<String>());

    assertThat(result.size(), is(0));
  }

  @Test
  public void loadListWithOneEntity() {

    store(1l, new Entity(1l));

    EntityLoader entityLoader = createEntityLoader();

    List<Entity> result = entityLoader.loadAll(Entity.class, Arrays.asList(String.valueOf(1l)));

    assertThat(result.size(), is(1));
    assertThat(result.get(0).id, is(1l));
  }

  @Test
  public void loadListWithTwoEntities() {

    store(1l, new Entity(1l));
    store(2l, new Entity(2l));

    EntityLoader entityLoader = createEntityLoader();

    List<Entity> result = entityLoader.loadAll(Entity.class, Arrays.asList(String.valueOf(1l), String.valueOf(2l)));

    assertThat(result.size(), is(2));
    assertThat(result.get(0).id, is(1l));
    assertThat(result.get(1).id, is(2l));
  }

  @Test
  public void loadingNotStoredEntity() {

    store(1l, new Entity(1l));

    EntityLoader entityLoader = createEntityLoader();

    List<Entity> result = entityLoader.loadAll(Entity.class, Arrays.asList(String.valueOf(2l)));

    assertThat(result.size(), is(0));
  }

  public abstract EntityLoader createEntityLoader();

  public abstract void store(Long id, Object object);

  class Entity {

    final Long id;

    public Entity(Long id) {
      this.id = id;
    }
  }
}

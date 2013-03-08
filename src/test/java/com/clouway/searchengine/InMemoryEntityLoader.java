package com.clouway.searchengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class InMemoryEntityLoader implements EntityLoader {

  Map<Class<?>, List<Long>> ids = new HashMap<Class<?>, List<Long>>();
  Map<Long, Object> objects = new HashMap<Long, Object>();

  public void store(Long id, Object object) {

    if (ids.containsKey(object.getClass())) {
      List<Long> longs = ids.get(object.getClass());
      longs.add(id);
    } else {

      Class<?> key = object.getClass();
      List<Long> value = new ArrayList<Long>();
      value.add(id);

      ids.put(key, value);
    }

    objects.put(id, object);
  }

  public <T> List<T> loadAll(Class<T> clazz, List<String> entityIds) {

    Set<Class<?>> classes = ids.keySet();

    List<T> result = new ArrayList<T>();

    for (Class<?> aClass : classes) {
      if (aClass.equals(clazz)) {
        List<Long> values = ids.get(aClass);

        for (Long value : values) {
          Object object = objects.get(value);
          result.add((T) object);
        }
      }
    }

    return result;
  }
}

package com.clouway.cuse;

import com.clouway.cuse.spi.EntityLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class InMemoryRepository implements EntityLoader {

  Map<Class<?>, List<Long>> classes = new HashMap<Class<?>, List<Long>>();
  Map<Long, Object> objects = new HashMap<Long, Object>();

  public void store(Long objectId, Object object) {

    if (classes.containsKey(object.getClass())) {
      List<Long> values = classes.get(object.getClass());
      values.add(objectId);
    } else {

      Class<?> aClass = object.getClass();
      List<Long> values = new ArrayList<Long>();
      values.add(objectId);

      classes.put(aClass, values);
    }

    objects.put(objectId, object);
  }

  public <T> List<T> loadAll(Class<T> clazz, List<String> entityIds) {

    List<T> result = new ArrayList<T>();

    if (classes.containsKey(clazz)) {

      List<Long> values = classes.get(clazz);

      for (String entityId : entityIds) {

        if (values.contains(Long.valueOf(entityId))) {
          Object object = objects.get(Long.valueOf(entityId));
          result.add((T) object);
        }
      }
    }

    return result;
  }
}

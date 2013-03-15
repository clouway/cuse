package com.clouway.cuse.spi;

import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public interface EntityLoader {

  <T> List<T> loadAll(Class<T> clazz, List<String> entityIds);
}

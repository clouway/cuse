package com.clouway.cuse.spi.filters;

import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public interface SearchFilter {

  String getValue(List<String> fields);
}

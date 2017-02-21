package com.clouway.cuse.spi;

import com.clouway.cuse.spi.annotations.Atomic;
import com.clouway.cuse.spi.annotations.SearchId;
import com.clouway.cuse.spi.annotations.SearchIndex;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
@SearchIndex(name = "AtomicTagsListIndex")
public class AtomicTagsListIndex {

  @SearchId
  public final Long id;

  @Atomic
  private Set<String> tags;

  public AtomicTagsListIndex(Long id, String...tags) {
    this.id = id;
    this.tags = Sets.newHashSet(tags);
  }
}

package com.clouway.cuse.spi;

import com.clouway.cuse.spi.annotations.Atomic;
import com.clouway.cuse.spi.annotations.SearchId;
import com.clouway.cuse.spi.annotations.SearchIndex;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
@SearchIndex(name = "AtomicTagsListIndex")
public class AtomicTagsListIndex {

  @SearchId
  public final Long id;

  public final List<String> refs;

  @Atomic
  private Set<String> tags;

  public AtomicTagsListIndex(Long id, String...tags) {
    this.id = id;
    this.tags = Sets.newHashSet(tags);
    refs = new ArrayList<String>();
  }

  public AtomicTagsListIndex(Long id, List<String> refs, String...tags) {
    this.id = id;
    this.refs = refs;
    this.tags = Sets.newHashSet(tags);
  }
}

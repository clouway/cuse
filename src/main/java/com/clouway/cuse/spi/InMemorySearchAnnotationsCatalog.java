package com.clouway.cuse.spi;

import com.clouway.cuse.spi.annotations.FullTextSearch;
import com.clouway.cuse.spi.annotations.FullWordSearch;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
public class InMemorySearchAnnotationsCatalog implements SearchAnnotationsCatalog {

  Set<SearchAnnotation> searchAnnotations = new HashSet<SearchAnnotation>(){{
    add(new SearchAnnotation() {
      @Override
      public Class<? extends Annotation> getAnnotationClass() {
        return FullTextSearch.class;
      }

      @Override
      public boolean isFullTextSearch() {
        return true;
      }
    });

    add(new SearchAnnotation() {
      @Override
      public Class<? extends Annotation> getAnnotationClass() {
        return FullWordSearch.class;
      }

      @Override
      public boolean isFullTextSearch() {
        return false;
      }
    });
  }};


  @Override
  public Set<SearchAnnotation> getSearchAnnotations() {
    return searchAnnotations;
  }
}

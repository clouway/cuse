package com.clouway.cuse.spi;

import java.lang.annotation.Annotation;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
public interface SearchAnnotation {

  Class<? extends Annotation> getAnnotationClass();

  boolean isFullTextSearch();

}

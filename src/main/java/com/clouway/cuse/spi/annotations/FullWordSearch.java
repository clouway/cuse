package com.clouway.cuse.spi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Declare which field will be used for search by word<br/>
 * Using
 * <pre>
 *
 *   class Foo {
 *
 *     @  FullWordSearch
 *     private String bar = "some bar";
 *
 *     ...
 *   }
 *
 * </pre>
 *
 * Representation in {@link com.google.appengine.api.search.Document} look like this
 * <pre>
 *  Document(
 *     bar = "some"
 *     bar = "bar"
 *  )
 * </pre>
 *
 * Without this annotation will be used default representation and {@link com.google.appengine.api.search.Document} will look like this
 *
 * <pre>
 *  Document(
 *     bar = "some bar"
 *  )
 * </pre>
 *
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface FullWordSearch {
}

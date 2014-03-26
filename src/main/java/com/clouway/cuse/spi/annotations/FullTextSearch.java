package com.clouway.cuse.spi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Declare which field will be used for full text search<br/>
 * Using
 * <pre>
 *
 *   class Foo {
 *
 *     @  FullTextSearch
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
 *     bar = "s"
 *     bar = "om"
 *     bar = "me"
 *     bar = "some"
 *     bar = "b"
 *     bar = "ba"
 *     bar = "bar"
 *     ...
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
public @interface FullTextSearch {
}

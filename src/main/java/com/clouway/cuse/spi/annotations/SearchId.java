package com.clouway.cuse.spi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Declare which field will be used for id of index. The id of index can be any type for which have
 * declared {@link com.clouway.cuse.spi.IdConverter}. By default exist  IdConverters for String and Long.
 * <br/>
 * Using
 *
 * <pre>
 *
 *   class Foo {
 *
 *     @ SearchId
 *     private String id;
 *
 *     ...
 *   }
 *
 * </pre>
 * <pre>
 *
 *   class Foo {
 *
 *     @ SearchId
 *     private Long id;
 *
 *     ...
 *   }
 *
 * </pre>
 *
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface SearchId {
}

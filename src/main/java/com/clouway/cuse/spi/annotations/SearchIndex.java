package com.clouway.cuse.spi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Declare index name for given class <br/>
 * Using
 * <pre>
 *   @ SearchIndex(name = "FooIndex")
 *   class Foo {
 *     ...
 *   }
 *
 * </pre>
 *
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SearchIndex {

   public String name();

}

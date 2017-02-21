package com.clouway.cuse.spi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Declare which field will be stored as atomic<br/>
 * Usage :
 * <pre>
 *
 *   class Foo {
 *
 *     @Atomic
 *     private String bar = "some bar";
 *
 *     ...
 *   }
 * @author Mihail Lesikov (mihail.lesikovWclouway.com)
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Atomic {
}

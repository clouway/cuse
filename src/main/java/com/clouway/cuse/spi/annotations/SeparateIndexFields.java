package com.clouway.cuse.spi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface SeparateIndexFields {
}

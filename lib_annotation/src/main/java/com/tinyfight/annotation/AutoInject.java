package com.tinyfight.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by tinyfight on 2018/1/23.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface AutoInject {
    Class<?> presenter();
    Class<?> contract();
}

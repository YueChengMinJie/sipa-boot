package com.sipa.boot.mqttv3.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author caszhou
 * @date 2022/6/23
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NamedValue {
    /**
     * Parameter name.
     *
     * @return Parameter name.
     */
    String value();

    /**
     * if required is true and value is null, method does not execute.
     * 
     * @return boolean
     */
    boolean required() default false;
}

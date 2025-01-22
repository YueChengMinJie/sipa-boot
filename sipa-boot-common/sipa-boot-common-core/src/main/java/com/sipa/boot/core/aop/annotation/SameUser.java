package com.sipa.boot.core.aop.annotation;

import java.lang.annotation.*;

/**
 * @author 甘华根
 * @since 2020/7/31 14:08
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SameUser {
    String module() default "";
}

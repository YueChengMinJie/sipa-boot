package com.sipa.boot.core.aop.annotation;

import java.lang.annotation.*;

/**
 * @author zhouxiajie
 * @date 2019-08-02
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PrintTime {
    //
}

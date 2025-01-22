package com.sipa.boot.mvc.codec.annotation;

import java.lang.annotation.*;

/**
 * 返回数据是否加密
 *
 * @author caszhou
 * @date 2020/9/18
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Encode {
    // noop
}

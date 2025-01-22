package com.sipa.boot.mvc.codec.annotation;

import java.lang.annotation.*;

/**
 * 组合注解，接受解密，返回加密
 *
 * @author caszhou
 * @date 2020/9/18
 */
@Encode
@Decode
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Encrypt {
    // noop
}

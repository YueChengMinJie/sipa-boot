package com.sipa.boot.mvc.response;

import java.lang.annotation.*;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoPackage {

}

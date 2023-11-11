package com.sipa.boot.extension;

import java.lang.annotation.*;

import org.springframework.stereotype.Component;

/**
 * @author caszhou
 * @date 2019/4/24
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Repeatable(Extensions.class)
@Component
public @interface Extension {
    String bizId() default BizScenario.DEFAULT_BIZ_ID;

    String useCase() default BizScenario.DEFAULT_USE_CASE;

    String scenario() default BizScenario.DEFAULT_SCENARIO;
}

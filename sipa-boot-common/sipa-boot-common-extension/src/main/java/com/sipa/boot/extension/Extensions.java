package com.sipa.boot.extension;

import java.lang.annotation.*;

import org.springframework.stereotype.Component;

/**
 * because {@link Extension} only supports single coordinates, this annotation is a supplement to {@link Extension} and
 * supports multiple coordinates
 *
 * @author caszhou
 * @date 2019/4/24
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Component
public @interface Extensions {
    String[] bizId() default BizScenario.DEFAULT_BIZ_ID;

    String[] useCase() default BizScenario.DEFAULT_USE_CASE;

    String[] scenario() default BizScenario.DEFAULT_SCENARIO;

    Extension[] value() default {};
}

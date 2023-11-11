package com.sipa.boot.mvc.allinone;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author caszhou
 * @date 2023/4/25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@RestController
@Validated
@Tag(name = "")
@RequestMapping
public @interface SipaRest {
    @AliasFor(annotation = Tag.class, attribute = "name")
    String tag() default "";

    @AliasFor(annotation = Validated.class, attribute = "value")
    Class<?>[] validationGroups() default {};

    @AliasFor(annotation = RestController.class, attribute = "value")
    String restControllerValue() default "";

    @AliasFor(annotation = RequestMapping.class, attribute = "name")
    String name() default "";

    @AliasFor(annotation = RequestMapping.class, attribute = "value")
    String[] value() default {};

    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] path() default {};

    @AliasFor(annotation = RequestMapping.class, attribute = "method")
    RequestMethod[] method() default {};

    @AliasFor(annotation = RequestMapping.class, attribute = "produces")
    String[] produces() default {};

    @AliasFor(annotation = RequestMapping.class, attribute = "consumes")
    String[] consumes() default {};

    @AliasFor(annotation = RequestMapping.class, attribute = "params")
    String[] params() default {};

    @AliasFor(annotation = RequestMapping.class, attribute = "headers")
    String[] headers() default {};
}

package com.sipa.boot.feign.allinone;

import java.lang.annotation.*;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.annotation.AliasFor;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author caszhou
 * @date 2023/4/25
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
@FeignClient
@Validated
@Tag(name = "")
public @interface SipaApi {
    @AliasFor(annotation = FeignClient.class, attribute = "name")
    String name();

    @AliasFor(annotation = FeignClient.class, attribute = "contextId")
    String contextId();

    @AliasFor(annotation = FeignClient.class, attribute = "url")
    String url() default "";

    @AliasFor(annotation = FeignClient.class, attribute = "path")
    String path() default "";

    @AliasFor(annotation = FeignClient.class, attribute = "configuration")
    Class<?>[] configuration() default {};

    @AliasFor(annotation = FeignClient.class, attribute = "fallback")
    Class<?> fallback() default void.class;

    @AliasFor(annotation = FeignClient.class, attribute = "fallbackFactory")
    Class<?> fallbackFactory() default void.class;

    @AliasFor(annotation = Validated.class, attribute = "value")
    Class<?>[] validationGroups() default {};

    @AliasFor(annotation = Tag.class, attribute = "name")
    String tag();

    @AliasFor(annotation = Tag.class, attribute = "description")
    String description() default "";

    @AliasFor(annotation = Tag.class, attribute = "externalDocs")
    ExternalDocumentation externalDocs() default @ExternalDocumentation();

    @AliasFor(annotation = Tag.class, attribute = "extensions")
    Extension[] extensions() default {};
}

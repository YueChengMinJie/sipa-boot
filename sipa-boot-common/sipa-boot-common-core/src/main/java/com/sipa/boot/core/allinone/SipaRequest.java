package com.sipa.boot.core.allinone;

import java.lang.annotation.*;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sipa.boot.core.constant.SipaConstant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * @author caszhou
 * @date 2023/4/25
 */
public interface SipaRequest {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Documented
    @RequestMapping(method = RequestMethod.GET)
    @Operation(security = {@SecurityRequirement(name = SipaConstant.AUTH_KEY)})
    @interface GetMapping {
        @AliasFor(annotation = RequestMapping.class, attribute = "name")
        String name() default "";

        @AliasFor(annotation = RequestMapping.class, attribute = "value")
        String[] value() default {};

        @AliasFor(annotation = RequestMapping.class, attribute = "path")
        String[] path() default {};

        @AliasFor(annotation = RequestMapping.class, attribute = "params")
        String[] params() default {};

        @AliasFor(annotation = RequestMapping.class, attribute = "headers")
        String[] headers() default {};

        @AliasFor(annotation = RequestMapping.class, attribute = "consumes")
        String[] consumes() default {};

        @AliasFor(annotation = RequestMapping.class, attribute = "produces")
        String[] produces() default {};

        @AliasFor(annotation = Operation.class, attribute = "summary")
        String summary();

        @AliasFor(annotation = Operation.class, attribute = "description")
        String description() default "";

        @AliasFor(annotation = Operation.class, attribute = "operationId")
        String operationId() default "";

        @AliasFor(annotation = Operation.class, attribute = "deprecated")
        boolean deprecated() default false;

        @AliasFor(annotation = Operation.class, attribute = "hidden")
        boolean hidden() default false;

        @AliasFor(annotation = Operation.class, attribute = "responses")
        ApiResponse[] responses() default {};

        @AliasFor(annotation = Operation.class, attribute = "parameters")
        Parameter[] parameters() default {};

        @AliasFor(annotation = Operation.class, attribute = "tags")
        String[] tags() default {};
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Documented
    @RequestMapping(method = RequestMethod.POST)
    @Operation(security = {@SecurityRequirement(name = SipaConstant.AUTH_KEY)})
    @interface PostMapping {
        @AliasFor(annotation = RequestMapping.class, attribute = "name")
        String name() default "";

        @AliasFor(annotation = RequestMapping.class, attribute = "value")
        String[] value() default {};

        @AliasFor(annotation = RequestMapping.class, attribute = "path")
        String[] path() default {};

        @AliasFor(annotation = RequestMapping.class, attribute = "params")
        String[] params() default {};

        @AliasFor(annotation = RequestMapping.class, attribute = "headers")
        String[] headers() default {};

        @AliasFor(annotation = RequestMapping.class, attribute = "consumes")
        String[] consumes() default {};

        @AliasFor(annotation = RequestMapping.class, attribute = "produces")
        String[] produces() default {};

        @AliasFor(annotation = Operation.class, attribute = "summary")
        String summary();

        @AliasFor(annotation = Operation.class, attribute = "description")
        String description() default "";

        @AliasFor(annotation = Operation.class, attribute = "operationId")
        String operationId() default "";

        @AliasFor(annotation = Operation.class, attribute = "deprecated")
        boolean deprecated() default false;

        @AliasFor(annotation = Operation.class, attribute = "hidden")
        boolean hidden() default false;

        @AliasFor(annotation = Operation.class, attribute = "responses")
        ApiResponse[] responses() default {};

        @AliasFor(annotation = Operation.class, attribute = "parameters")
        Parameter[] parameters() default {};

        @AliasFor(annotation = Operation.class, attribute = "tags")
        String[] tags() default {};
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Documented
    @RequestMapping(method = RequestMethod.PUT)
    @Operation(security = {@SecurityRequirement(name = SipaConstant.AUTH_KEY)})
    @interface PutMapping {
        @AliasFor(annotation = RequestMapping.class, attribute = "name")
        String name() default "";

        @AliasFor(annotation = RequestMapping.class, attribute = "value")
        String[] value() default {};

        @AliasFor(annotation = RequestMapping.class, attribute = "path")
        String[] path() default {};

        @AliasFor(annotation = RequestMapping.class, attribute = "params")
        String[] params() default {};

        @AliasFor(annotation = RequestMapping.class, attribute = "headers")
        String[] headers() default {};

        @AliasFor(annotation = RequestMapping.class, attribute = "consumes")
        String[] consumes() default {};

        @AliasFor(annotation = RequestMapping.class, attribute = "produces")
        String[] produces() default {};

        @AliasFor(annotation = Operation.class, attribute = "summary")
        String summary();

        @AliasFor(annotation = Operation.class, attribute = "description")
        String description() default "";

        @AliasFor(annotation = Operation.class, attribute = "operationId")
        String operationId() default "";

        @AliasFor(annotation = Operation.class, attribute = "deprecated")
        boolean deprecated() default false;

        @AliasFor(annotation = Operation.class, attribute = "hidden")
        boolean hidden() default false;

        @AliasFor(annotation = Operation.class, attribute = "responses")
        ApiResponse[] responses() default {};

        @AliasFor(annotation = Operation.class, attribute = "parameters")
        Parameter[] parameters() default {};

        @AliasFor(annotation = Operation.class, attribute = "tags")
        String[] tags() default {};
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Documented
    @RequestMapping(method = RequestMethod.DELETE)
    @Operation(security = {@SecurityRequirement(name = SipaConstant.AUTH_KEY)})
    @interface DeleteMapping {
        @AliasFor(annotation = RequestMapping.class, attribute = "name")
        String name() default "";

        @AliasFor(annotation = RequestMapping.class, attribute = "value")
        String[] value() default {};

        @AliasFor(annotation = RequestMapping.class, attribute = "path")
        String[] path() default {};

        @AliasFor(annotation = RequestMapping.class, attribute = "params")
        String[] params() default {};

        @AliasFor(annotation = RequestMapping.class, attribute = "headers")
        String[] headers() default {};

        @AliasFor(annotation = RequestMapping.class, attribute = "consumes")
        String[] consumes() default {};

        @AliasFor(annotation = RequestMapping.class, attribute = "produces")
        String[] produces() default {};

        @AliasFor(annotation = Operation.class, attribute = "summary")
        String summary();

        @AliasFor(annotation = Operation.class, attribute = "description")
        String description() default "";

        @AliasFor(annotation = Operation.class, attribute = "operationId")
        String operationId() default "";

        @AliasFor(annotation = Operation.class, attribute = "deprecated")
        boolean deprecated() default false;

        @AliasFor(annotation = Operation.class, attribute = "hidden")
        boolean hidden() default false;

        @AliasFor(annotation = Operation.class, attribute = "responses")
        ApiResponse[] responses() default {};

        @AliasFor(annotation = Operation.class, attribute = "parameters")
        Parameter[] parameters() default {};

        @AliasFor(annotation = Operation.class, attribute = "tags")
        String[] tags() default {};
    }
}

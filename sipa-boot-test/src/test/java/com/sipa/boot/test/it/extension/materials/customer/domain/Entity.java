package com.sipa.boot.test.it.extension.materials.customer.domain;

import java.lang.annotation.*;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Entity, Entity Object is prototype and is not thread-safe
 *
 * @author caszhou
 * @date 2019/4/24
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public @interface Entity {}

package com.sipa.boot.core.env;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import lombok.SneakyThrows;

/**
 * @author caszhou
 * @date 2022-09-14
 */
public abstract class EnvApplicationContextInitializer extends EnvProcessor
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    @SneakyThrows
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (!(applicationContext instanceof AnnotationConfigApplicationContext)) {
            process(applicationContext.getEnvironment());
        }
    }
}

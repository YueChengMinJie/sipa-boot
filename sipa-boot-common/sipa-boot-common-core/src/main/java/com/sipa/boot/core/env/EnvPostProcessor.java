package com.sipa.boot.core.env;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.cloud.bootstrap.BootstrapApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

import lombok.SneakyThrows;

/**
 * @author caszhou
 * @date 2022-09-14
 */
public abstract class EnvPostProcessor extends EnvProcessor implements EnvironmentPostProcessor {
    @Override
    @SneakyThrows
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (environment.getClass().getName().startsWith(BootstrapApplicationListener.class.getName())) {
            process(environment);
        }
    }

    @Override
    protected boolean isEnvLogShown() {
        return false;
    }
}

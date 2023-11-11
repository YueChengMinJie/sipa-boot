package com.sipa.boot.core.env;

import java.util.Objects;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import com.sipa.boot.core.constant.SipaConstant;

/**
 * @author caszhou
 * @date 2023/6/1
 */
@Order
public class EnvironmentCustomizer implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (Objects.nonNull(environment.getPropertySources().get(SipaConstant.Env.DEFAULT_PROPERTY_SOURCE))) {
            return;
        }

        Properties properties = this.getSipaCloudVersion();
        PropertiesPropertySource pps =
            new PropertiesPropertySource(SipaConstant.Env.DEFAULT_PROPERTY_SOURCE, properties);
        environment.getPropertySources().addLast(pps);
    }

    private Properties getSipaCloudVersion() {
        Properties properties = new Properties();
        properties.setProperty(SipaConstant.Env.VERSION, StringUtils.defaultString(
            EnvironmentCustomizer.class.getPackage().getImplementationVersion(), SipaConstant.Env.DEFAULT_VERSION));
        return properties;
    }
}

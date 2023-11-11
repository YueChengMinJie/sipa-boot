package com.sipa.boot.extension;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author caszhou
 * @date 2023/2/18
 */
@Configuration
@ConditionalOnClass({Extension.class})
@ComponentScan(value = {"com.sipa.boot.extension"})
public class ExtensionAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(ExtensionRegister.class)
    public ExtensionRegister register() {
        return new ExtensionRegister();
    }

    @Bean(initMethod = "init")
    @ConditionalOnMissingBean(ExtensionBootstrap.class)
    public ExtensionBootstrap bootstrap() {
        return new DefaultExtensionBootstrap();
    }

    @Bean
    @ConditionalOnMissingBean(ExtensionRepository.class)
    public ExtensionRepository repository() {
        return new DefaultExtensionRepository();
    }

    @Bean
    @ConditionalOnMissingBean(ExtensionExecutor.class)
    public ExtensionExecutor executor() {
        return new ExtensionExecutor();
    }
}

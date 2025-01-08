package com.sipa.boot.extension.ark;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.sipa.boot.extension.ExtensionAutoConfiguration;
import com.sipa.boot.extension.ExtensionBootstrap;

/**
 * @author caszhou
 * @date 2023/2/18
 */
@Configuration
@ConditionalOnClass({ArkExtensionBootstrap.class})
@AutoConfigureBefore(ExtensionAutoConfiguration.class)
@ComponentScan("com.sipa.boot.extension.ark")
public class ArkExtensionAutoConfiguration {
    @Bean(initMethod = "init")
    @ConditionalOnMissingBean(ExtensionBootstrap.class)
    public ExtensionBootstrap bootstrap() {
        return new ArkExtensionBootstrap();
    }
}

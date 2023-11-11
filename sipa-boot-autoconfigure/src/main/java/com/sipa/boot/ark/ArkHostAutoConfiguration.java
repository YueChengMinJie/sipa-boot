package com.sipa.boot.ark;

import com.sipa.boot.ark.property.ArkHostProperty;
import com.sipa.boot.core.property.YamlPropertySourceFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Configuration
@ConditionalOnClass(ArkHostProperty.class)
@EnableConfigurationProperties(ArkHostProperty.class)
@PropertySource(value = "classpath:host.yml", factory = YamlPropertySourceFactory.class)
public class ArkHostAutoConfiguration {

}

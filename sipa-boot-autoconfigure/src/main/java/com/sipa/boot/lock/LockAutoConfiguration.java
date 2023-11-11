package com.sipa.boot.lock;

import com.sipa.boot.lock.property.LockProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author caszhou
 * @date 2023/4/20
 */
@Configuration
@ConditionalOnClass({LockProperty.class})
@EnableConfigurationProperties(LockProperty.class)
public class LockAutoConfiguration {

}

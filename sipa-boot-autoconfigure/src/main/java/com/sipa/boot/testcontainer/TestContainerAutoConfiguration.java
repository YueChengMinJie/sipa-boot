package com.sipa.boot.testcontainer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author caszhou
 * @date 2023/2/18
 */
@Configuration
@ConditionalOnClass({TestContainer.class})
@ComponentScan("com.sipa.boot.testcontainer.**")
public class TestContainerAutoConfiguration {

}

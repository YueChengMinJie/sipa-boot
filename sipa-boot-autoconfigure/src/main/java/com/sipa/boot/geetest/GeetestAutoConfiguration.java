package com.sipa.boot.geetest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.sipa.boot.geetest.property.GeetestProperty;

import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Slf4j
@Configuration
@ConditionalOnClass(GeetestProperty.class)
@ComponentScan("com.sipa.boot.geetest.**")
@EnableConfigurationProperties(GeetestProperty.class)
public class GeetestAutoConfiguration {

}

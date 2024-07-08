package com.sipa.boot.mqttv3;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipa.boot.mqttv3.core.MqttAdapter;
import com.sipa.boot.mqttv3.core.MqttConnector;
import com.sipa.boot.mqttv3.core.MqttConversionService;
import com.sipa.boot.mqttv3.core.MqttSubscribeProcessor;
import com.sipa.boot.mqttv3.property.MqttProperty;
import com.sipa.boot.mqttv3.publisher.MqttPublisher;

import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2022/6/23
 */
@Slf4j
@Configuration
@ConditionalOnClass(MqttAsyncClient.class)
@ComponentScan(value = {"com.sipa.boot.mqttv3.**"})
@EnableConfigurationProperties(value = MqttProperty.class)
public class MqttAutoConfiguration {
    public MqttAutoConfiguration(ListableBeanFactory beanFactory, ObjectMapper objectMapper) {
        MqttConversionService.addBeans(MqttConversionService.getSharedInstance(), beanFactory);
    }

    @Bean
    @ConditionalOnMissingBean(MqttAdapter.class)
    public MqttAdapter mqttAdapter() {
        log.info("init mqttAdapter");
        return new MqttAdapter();
    }

    @Bean
    @ConditionalOnMissingBean(MqttPublisher.class)
    public MqttPublisher mqttPublisher() {
        log.info("init mqttPublisher");
        return new MqttPublisher();
    }

    @Bean
    @ConditionalOnMissingBean(MqttSubscribeProcessor.class)
    public MqttSubscribeProcessor mqttSubscribeProcessor() {
        log.info("init mqttSubscribeProcessor");
        return new MqttSubscribeProcessor();
    }

    @Bean
    @ConditionalOnMissingBean(MqttConnector.class)
    public MqttConnector mqttConnector(MqttProperty properties, MqttAdapter mqttAdapter) {
        log.info("init mqttConnector");
        MqttConnector connector = new MqttConnector();
        connector.start(properties, mqttAdapter);
        return connector;
    }
}

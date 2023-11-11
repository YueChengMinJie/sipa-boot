package com.sipa.boot.rocketmq;

import com.sipa.boot.rocketmq.env.RocketMQEnvProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;

import com.alibaba.cloud.stream.binder.rocketmq.autoconfigurate.ExtendedBindingHandlerMappingsProviderConfiguration;
import com.alibaba.cloud.stream.binder.rocketmq.convert.RocketMQMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Configuration
@AllArgsConstructor
@ConditionalOnClass(RocketMQEnvProcessor.class)
@AutoConfigureAfter(ExtendedBindingHandlerMappingsProviderConfiguration.class)
public class RocketMQAutoConfiguration {
    private final ObjectMapper objectMapper;

    @Bean(RocketMQMessageConverter.DEFAULT_NAME)
    public CompositeMessageConverter rocketMQMessageConverter() {
        CompositeMessageConverter messageConverter = new RocketMQMessageConverter().getMessageConverter();
        messageConverter.getConverters()
            .stream()
            .filter(converter -> converter.getClass().equals(MappingJackson2MessageConverter.class))
            .findFirst()
            .ifPresent(converter -> {
                MappingJackson2MessageConverter jacksonConvert = (MappingJackson2MessageConverter)converter;
                jacksonConvert.setObjectMapper(objectMapper);
            });
        return messageConverter;
    }

    @Bean
    @Primary
    public MessageConverter rocketMQCustomMessageConverter() {
        RocketMQMessageConverter rocketMQMessageConverter = new RocketMQMessageConverter();
        CompositeMessageConverter messageConverter = rocketMQMessageConverter.getMessageConverter();
        messageConverter.getConverters()
            .stream()
            .filter(converter -> converter.getClass().equals(MappingJackson2MessageConverter.class))
            .findFirst()
            .ifPresent(converter -> {
                MappingJackson2MessageConverter jacksonConvert = (MappingJackson2MessageConverter)converter;
                jacksonConvert.setObjectMapper(objectMapper);
            });
        return rocketMQMessageConverter;
    }
}

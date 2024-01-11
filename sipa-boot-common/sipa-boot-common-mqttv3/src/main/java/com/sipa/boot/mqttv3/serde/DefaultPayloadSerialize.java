package com.sipa.boot.mqttv3.serde;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2024/1/10
 */
@Slf4j
@Component
@AllArgsConstructor
public class DefaultPayloadSerialize implements PayloadSerialize {
    private final ObjectMapper objectMapper;

    @Override
    public byte[] convert(@NotNull Object source) {
        try {
            return objectMapper.writeValueAsBytes(source);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.warn("Payload serialize error", e);
        }
        return null;
    }

    @NotNull
    @Override
    public <U> Converter<Object, U> andThen(@NotNull Converter<? super byte[], ? extends U> after) {
        return PayloadSerialize.super.andThen(after);
    }
}

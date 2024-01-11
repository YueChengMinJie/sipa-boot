package com.sipa.boot.mqttv3.serde;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
public class DefaultPayloadDeserialize implements PayloadDeserialize {
    private final ObjectMapper objectMapper;

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public <T> Converter<byte[], T> getConverter(@NotNull Class<T> targetType) {
        return source -> {
            try {
                if (targetType == String.class) {
                    return (T)new String(source, StandardCharsets.UTF_8);
                }
                return objectMapper.readValue(source, targetType);
            } catch (IOException e) {
                log.warn("Payload deserialize error", e);
            }
            return null;
        };
    }
}

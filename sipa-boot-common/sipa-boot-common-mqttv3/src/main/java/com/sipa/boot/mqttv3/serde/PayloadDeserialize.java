package com.sipa.boot.mqttv3.serde;

import org.springframework.core.convert.converter.ConverterFactory;

/**
 * @author caszhou
 * @date 2022/6/23
 */
public interface PayloadDeserialize extends ConverterFactory<byte[], Object> {
    //
}

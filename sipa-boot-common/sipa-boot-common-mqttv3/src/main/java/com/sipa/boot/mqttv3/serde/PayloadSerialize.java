package com.sipa.boot.mqttv3.serde;

import org.springframework.core.convert.converter.Converter;

/**
 * @author caszhou
 * @date 2022/6/23
 */
public interface PayloadSerialize extends Converter<Object, byte[]> {
    //
}

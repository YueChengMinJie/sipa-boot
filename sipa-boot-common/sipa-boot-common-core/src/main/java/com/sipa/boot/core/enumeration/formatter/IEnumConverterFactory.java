package com.sipa.boot.core.enumeration.formatter;

import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.google.common.collect.Maps;

/**
 * @author caszhou
 * @date 2019-01-22
 */
public class IEnumConverterFactory implements ConverterFactory<String, IEnum<?>> {
    private final Map<Class<?>, Converter<String, ? extends IEnum<?>>> converterMap = Maps.newHashMap();

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IEnum<?>> Converter<String, T> getConverter(Class<T> targetType) {
        Converter<String, T> converter = (Converter<String, T>)converterMap.get(targetType);
        if (converter == null) {
            converter = new IEnumConverter<>(targetType);
            converterMap.put(targetType, converter);
        }
        return converter;
    }
}

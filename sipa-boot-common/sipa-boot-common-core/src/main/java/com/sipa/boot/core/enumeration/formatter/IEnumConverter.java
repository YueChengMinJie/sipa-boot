package com.sipa.boot.core.enumeration.formatter;

import java.util.Map;
import java.util.stream.Stream;

import org.springframework.core.convert.converter.Converter;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.google.common.collect.Maps;

/**
 * @author caszhou
 * @date 2019-01-22
 */
public class IEnumConverter<T extends IEnum<?>> implements Converter<String, T> {
    private final Map<String, T> enumMap = Maps.newHashMap();

    IEnumConverter(Class<T> enumType) {
        // parallel() 线程不安全，HashMap是线程不安全的类，可能会造成resize无限循环，元素丢失
        Stream.of(enumType.getEnumConstants()).forEach(obj -> enumMap.put(String.valueOf(obj.getValue()), obj));
    }

    @Override
    public T convert(String source) {
        T t = enumMap.get(source);
        if (t == null) {
            throw new IllegalArgumentException("No element matches " + source);
        }
        return t;
    }
}

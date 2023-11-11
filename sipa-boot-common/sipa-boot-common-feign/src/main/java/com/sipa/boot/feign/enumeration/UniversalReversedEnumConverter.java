package com.sipa.boot.feign.enumeration;

import org.springframework.core.convert.converter.Converter;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * @author caszhou
 * @date 2019-02-03
 */
public class UniversalReversedEnumConverter implements Converter<IEnum, String> {
    @Override
    public String convert(IEnum source) {
        return String.valueOf(source.getValue());
    }
}

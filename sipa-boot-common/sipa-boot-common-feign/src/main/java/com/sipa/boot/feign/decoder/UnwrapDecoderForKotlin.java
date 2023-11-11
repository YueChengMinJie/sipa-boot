package com.sipa.boot.feign.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hutool.extra.spring.SpringUtil;

/**
 * @author caszhou
 * @date 2019/3/1
 */
public class UnwrapDecoderForKotlin extends UnwrapDecoder {
    public UnwrapDecoderForKotlin() {
        super(SpringUtil.getBean(ObjectMapper.class));
    }
}

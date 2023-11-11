package com.sipa.boot.core.util;

import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hutool.extra.spring.SpringUtil;

/**
 * Spring 环境下可用
 *
 * @author zhouxiajie
 * @date 2019-03-19
 */
public class SipaJsonUtil {
    public static String writeValueAsString(Object object) {
        return writeValueAsString(SpringUtil.getBean(ObjectMapper.class), object, false);
    }

    public static String writeValueAsPrettyString(Object object) {
        return writeValueAsString(SpringUtil.getBean(ObjectMapper.class), object, true);
    }

    public static <T> T convertValue(String jsonStr, Class<T> clazz) {
        return convertValue(SpringUtil.getBean(ObjectMapper.class), jsonStr, clazz);
    }

    public static <T> T convertValue(String jsonStr, TypeReference<T> javaType) {
        try {
            return SpringUtil.getBean(ObjectMapper.class).readValue(jsonStr, javaType);
        } catch (JsonProcessingException ignored) {
            return null;
        }
    }

    private static String writeValueAsString(ObjectMapper objectMapper, Object object, boolean pretty) {
        try {
            if (Objects.nonNull(object)) {
                if (pretty) {
                    return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
                } else {
                    return objectMapper.writeValueAsString(object);
                }
            }
        } catch (JsonProcessingException ignored) {
        }
        return null;
    }

    private static <T> T convertValue(ObjectMapper objectMapper, String jsonStr, Class<T> clazz) {
        try {
            if (Objects.nonNull(jsonStr)) {
                return objectMapper.readValue(jsonStr, clazz);
            }
        } catch (JsonProcessingException ignored) {
        }
        return null;
    }
}

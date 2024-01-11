package com.sipa.boot.mqttv3.subscriber;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedList;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.convert.converter.Converter;

import com.sipa.boot.mqttv3.annotation.NamedValue;
import com.sipa.boot.mqttv3.annotation.Payload;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2022/6/23
 */
@Slf4j
@Getter
final class ParameterModel {
    /**
     * 标记为消息内容, 若参数为String类型, 并且无标记, 则赋值topic.
     */
    private boolean sign;

    private boolean required;

    private Class<?> type;

    private String name;

    private Object defaultValue;

    private LinkedList<Converter<Object, Object>> converters;

    private ParameterModel() {
        //
    }

    public static LinkedList<ParameterModel> of(Method method) {
        LinkedList<ParameterModel> parameters = new LinkedList<>();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterTypes.length; i++) {
            ParameterModel model = new ParameterModel();
            parameters.add(model);
            model.type = parameterTypes[i];
            model.defaultValue = defaultValue(model.type);
            Annotation[] annotations = parameterAnnotations[i];
            if (ArrayUtils.isNotEmpty(annotations)) {
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType() == NamedValue.class) {
                        NamedValue namedValue = (NamedValue)annotation;
                        model.required = model.required || namedValue.required();
                        model.name = namedValue.value();
                    }
                    if (annotation.annotationType() == Payload.class) {
                        Payload payload = (Payload)annotation;
                        model.sign = true;
                        model.required = model.required || payload.required();
                        model.converters = toConverters(payload.value());
                    }
                }
            }
        }
        return parameters;
    }

    private static Object defaultValue(Class<?> type) {
        if (type.isPrimitive()) {
            if (type == boolean.class) {
                return false;
            }
            if (type == char.class) {
                return (char)0;
            }
            if (type == byte.class) {
                return (byte)0;
            }
            if (type == short.class) {
                return (short)0;
            }
            if (type == int.class) {
                return 0;
            }
            if (type == long.class) {
                return 0L;
            }
            if (type == float.class) {
                return 0.0f;
            }
            if (type == double.class) {
                return 0.0d;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static LinkedList<Converter<Object, Object>> toConverters(Class<? extends Converter<?, ?>>[] classes) {
        if (classes == null || classes.length == 0) {
            return null;
        } else {
            LinkedList<Converter<Object, Object>> converters = new LinkedList<>();
            for (Class<? extends Converter<?, ?>> covert : classes) {
                try {
                    converters.add((Converter<Object, Object>)covert.getDeclaredConstructor().newInstance());
                } catch (Exception e) {
                    log.error("Create converter instance failed.", e);
                }
            }
            return converters;
        }
    }
}

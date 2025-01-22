package com.sipa.boot.mvc.codec.util;

import java.lang.annotation.Annotation;

import org.springframework.core.MethodParameter;

/**
 * @author caszhou
 * @date 2020/9/18
 */
public class ApiUtil {
    /**
     * 判断方法或类上有没有注解
     * 
     * @param method
     *            method对象
     * @param annotations
     *            注解类数组
     * @param <A>
     *            Annotation类型的class
     * @return boolean
     */
    public static <A extends Annotation> boolean hasMethodAnnotation(MethodParameter method, Class<A>[] annotations) {
        if (annotations != null) {
            for (Class<A> annotation : annotations) {
                if (method.hasMethodAnnotation(annotation)
                    || method.getDeclaringClass().isAnnotationPresent(annotation)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String cleanKey(String key) {
        return key.replaceAll("[\r\n]+", "")
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("-----BEGIN RSA PRIVATE KEY-----", "")
            .replace("-----END RSA PRIVATE KEY-----", "");
    }
}

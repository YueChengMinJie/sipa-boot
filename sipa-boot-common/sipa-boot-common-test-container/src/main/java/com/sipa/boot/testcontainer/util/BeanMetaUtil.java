package com.sipa.boot.testcontainer.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author caszhou
 * @date 2019/4/24
 */
public class BeanMetaUtil {
    public static Method findMethod(Class<?> clazz, Class<? extends Annotation> annotationType) {
        Method[] allMethods = clazz.getMethods();
        for (Method method : allMethods) {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation item : annotations) {
                if (item.annotationType().equals(annotationType)) {
                    return method;
                }
            }
        }
        return null;
    }
}

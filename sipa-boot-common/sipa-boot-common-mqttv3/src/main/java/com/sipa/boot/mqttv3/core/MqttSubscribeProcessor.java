package com.sipa.boot.mqttv3.core;

import java.lang.reflect.Method;
import java.util.LinkedList;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.sipa.boot.mqttv3.annotation.MqttSubscribe;
import com.sipa.boot.mqttv3.subscriber.MqttSubscriber;

/**
 * 过滤出类中所有标识了@MqttSubscribe注解的方法
 *
 * @author caszhou
 * @date 2022/6/23
 */
public class MqttSubscribeProcessor implements BeanPostProcessor {

    // 扫描出的@MqttSubscribe方法集合
    static final LinkedList<MqttSubscriber> SUBSCRIBERS = new LinkedList<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, @NotNull String beanName) throws BeansException {
        Method[] methods = bean.getClass().getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(MqttSubscribe.class)) {
                SUBSCRIBERS.add(MqttSubscriber.of(bean, method));
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        return bean;
    }
}

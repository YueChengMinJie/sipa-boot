package com.sipa.boot.core.app;

import org.springframework.context.ConfigurableApplicationContext;

import com.sipa.boot.core.constant.TcpCloudConstant;

import lombok.extern.slf4j.Slf4j;

/**
 * 项目启动器，搞定环境变量问题
 *
 * @author caszhou
 * @date 2022-09-14
 */
@Slf4j
public class SipaApplication4ArkBiz extends SipaApplication4Java {
    public static ConfigurableApplicationContext run(String appName, Class<?> source, String... args) {
        setJavaProperty(appName);
        return SipaApplication4Java.run(appName, source, args);
    }

    private static void setJavaProperty(String appName) {
        System.setProperty(TcpCloudConstant.Core.SPRING_JMX_DEFAULT_DOMAIN_KEY, appName + System.currentTimeMillis());
    }
}

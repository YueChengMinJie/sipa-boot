package com.sipa.boot.core.app;

import org.springframework.context.ConfigurableApplicationContext;

import com.sipa.boot.core.constant.SipaBootConstant;

import lombok.extern.slf4j.Slf4j;

/**
 * 项目启动器，搞定环境变量问题
 *
 * @author caszhou
 * @date 2022-09-14
 */
@Slf4j
public class SipaApplication4ArkHost extends SipaApplication4Java {
    public static ConfigurableApplicationContext run(String appName, Class<?> source, String... args) {
        setJavaProperty();
        return SipaApplication4Java.run(appName, source, args);
    }

    private static void setJavaProperty() {
        System.setProperty(SipaBootConstant.Core.SOFA_ARK_HOST_KEY, SipaBootConstant.Core.SOFA_ARK_HOST_VALUE);
        System.setProperty(SipaBootConstant.Core.NETTY_KEY, SipaBootConstant.Core.NETTY_VALUE);
    }
}

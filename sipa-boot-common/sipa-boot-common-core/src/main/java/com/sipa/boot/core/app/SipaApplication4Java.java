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
public class SipaApplication4Java extends SipaApplication {
    public static ConfigurableApplicationContext run(String appName, Class<?> source, String... args) {
        setJavaProperty();
        return SipaApplication.run(appName, source, args);
    }

    private static void setJavaProperty() {
        System.setProperty(SipaBootConstant.Core.LIQUIBASE_KEY, SipaBootConstant.Core.LIQUIBASE_VALUE);
    }
}

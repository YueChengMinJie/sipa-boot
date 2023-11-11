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
public class SipaApplication4ArkHost extends SipaApplication4Java {
    public static ConfigurableApplicationContext run(String appName, Class<?> source, String... args) {
        setJavaProperty();
        return SipaApplication4Java.run(appName, source, args);
    }

    private static void setJavaProperty() {
        System.setProperty(TcpCloudConstant.Core.SOFA_ARK_HOST_KEY, TcpCloudConstant.Core.SOFA_ARK_HOST_VALUE);
        System.setProperty(TcpCloudConstant.Core.NETTY_KEY, TcpCloudConstant.Core.NETTY_VALUE);
    }
}

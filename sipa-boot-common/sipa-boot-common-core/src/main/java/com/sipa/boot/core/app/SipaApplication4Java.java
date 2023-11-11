package com.sipa.boot.core.app;

import org.springframework.context.ConfigurableApplicationContext;

import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.core.constant.TcpCloudConstant;

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
        System.setProperty(
            TcpCloudConstant.Feign.PREFIX + SipaConstant.Symbol.POINT + TcpCloudConstant.Feign.ENABLED_KEY,
            TcpCloudConstant.Feign.ENABLED_VALUE);
        System.setProperty(TcpCloudConstant.Core.LIQUIBASE_KEY, TcpCloudConstant.Core.LIQUIBASE_VALUE);
        System.setProperty(
            TcpCloudConstant.Cache.PREFIX + SipaConstant.Symbol.POINT + TcpCloudConstant.Cache.ENABLED_KEY,
            TcpCloudConstant.Cache.ENABLED_VALUE);
    }
}

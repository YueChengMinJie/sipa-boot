package com.sipa.boot.core.app;

import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * launcher 扩展 用于一些组件发现
 *
 * @author caszhou
 * @date 2022-09-14
 */
public interface LauncherService {
    /**
     * 启动时 处理 SpringApplicationBuilder
     *
     * @param builder
     *            SpringApplicationBuilder
     * @param appName
     *            SpringApplicationAppName
     * @param profile
     *            SpringApplicationProfile
     */
    void launcher(SpringApplicationBuilder builder, String appName, String profile);
}

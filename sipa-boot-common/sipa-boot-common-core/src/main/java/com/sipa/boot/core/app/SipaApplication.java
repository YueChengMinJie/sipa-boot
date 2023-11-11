package com.sipa.boot.core.app;

import java.util.*;
import java.util.function.Function;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.*;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.sipa.boot.core.constant.SipaBootConstant;
import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.core.env.EnvConstant;
import com.sipa.boot.core.env.EnvProvider;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.SystemUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 项目启动器，搞定环境变量问题
 *
 * @author caszhou
 * @date 2022-09-14
 */
@Slf4j
public class SipaApplication {
    /**
     * Create an application context java -jar app.jar --spring.profiles.active=prod --server.port=2333
     *
     * @param appName
     *            application name
     * @param source
     *            The sources
     * @return an application context created from the current state
     */
    public static ConfigurableApplicationContext run(String appName, Class<?> source, String... args) {
        setProperty(appName);
        SpringApplicationBuilder builder = createSpringApplicationBuilder(appName, source, args);
        return builder.run(args);
    }

    protected static void setProperty(String appName) {
        // property name
        System.setProperty(SipaBootConstant.Core.SPRING_APPLICATION_NAME_KEY, appName);
        // rocketmq client
        System.setProperty("rocketmq.client.logFileMaxIndex", "20");
        System.setProperty("rocketmq.client.logFileMaxSize", "67108864");
        System.setProperty("rocketmq.client.logLevel", "WARN");
    }

    public static SpringApplicationBuilder createSpringApplicationBuilder(String appName, Class<?> source,
        String... args) {
        Assert.hasText(appName, SipaBootConstant.Core.SPRING_APPLICATION_NAME_KEY + "不能为空");

        // 读取环境变量，使用spring boot的规则
        ConfigurableEnvironment environment = new StandardEnvironment();
        MutablePropertySources propertySources = environment.getPropertySources();
        propertySources.addFirst(new SimpleCommandLinePropertySource(args));
        propertySources.addLast(new MapPropertySource(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME,
            environment.getSystemProperties()));
        propertySources.addLast(new SystemEnvironmentPropertySource(
            StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, environment.getSystemEnvironment()));

        // 获取配置的环境变量
        String[] activeProfiles = environment.getActiveProfiles();
        // 判断环境: local、dev、fat、prod
        List<String> profiles = Arrays.asList(activeProfiles);
        // 预设的环境
        List<String> presetProfiles = new ArrayList<>(
            Arrays.asList(EnvConstant.ENV_LOCAL, EnvConstant.ENV_DEV, EnvConstant.ENV_FAT, EnvConstant.ENV_PROD));
        // 交集
        boolean change = presetProfiles.retainAll(profiles);
        if (!change) {
            throw new RuntimeException("同时存在环境变量: " + JSONUtil.toJsonStr(presetProfiles));
        }
        // 当前使用
        List<String> activeProfileList = new ArrayList<>(profiles);
        Function<Object[], String> joinFun = StringUtils::arrayToCommaDelimitedString;
        SpringApplicationBuilder builder = new SpringApplicationBuilder(source);
        String profile;
        if (activeProfileList.isEmpty()) {
            profile = EnvConstant.ENV_LOCAL;
            activeProfileList.add(profile);
            builder.profiles(profile);
        } else if (activeProfileList.size() == 1) {
            profile = activeProfileList.get(0);
        } else if (presetProfiles.size() == 1) {
            profile = presetProfiles.get(0);
        } else {
            throw new RuntimeException(
                "同时存在多个环境变量, 激活的profile = [" + StringUtils.arrayToCommaDelimitedString(activeProfiles) + "]");
        }
        // 所有profiles和class的位置
        String activeProfile = joinFun.apply(activeProfileList.toArray());
        String startJarPath = Objects.requireNonNull(SipaApplication.class.getResource(SipaConstant.Symbol.SLASH))
            .getPath()
            .split(SipaConstant.Symbol.BEACON)[SipaConstant.Number.INT_0];
        log.info("微服务启动中");
        log.info("域名（地域）: [{}]", EnvProvider.getDomain());
        log.info("区域（中心）: [{}]", EnvProvider.getRegion());
        log.info("环境变量: [{}]", EnvProvider.getEnv());
        log.info("激活的spring配置: [{}]", activeProfile);
        log.info("classpath文件地址: [{}]", startJarPath);
        setAllProperty(appName, profile);

        // 加载自定义组件
        ServiceLoader<LauncherService> loader = ServiceLoader.load(LauncherService.class);
        loader.forEach(launcherService -> launcherService.launcher(builder, appName, profile));
        return builder;
    }

    protected static void setAllProperty(String appName, String profile) {
        Properties props = System.getProperties();
        // spring
        setPropIfNotExist(props, SipaBootConstant.Core.SPRING_PROFILES_ACTIVE_KEY, profile);
        setPropIfNotExist(props, SipaBootConstant.Core.ALLOW_BEAN_DEFINITION_OVERRIDING_KEY,
            SipaBootConstant.Core.ALLOW_BEAN_DEFINITION_OVERRIDING_VALUE);
        setPropIfNotExist(props, SipaBootConstant.Core.ALLOW_CIRCULAR_REFERENCES_KEY,
            SipaBootConstant.Core.ALLOW_CIRCULAR_REFERENCES_VALUE);
        // springdoc
        if (profile.equals(EnvConstant.ENV_FAT) || profile.equals(EnvConstant.ENV_PROD)) {
            setPropIfNotExist(props, SipaBootConstant.Core.SPRINGDOC_KEY, SipaBootConstant.Core.SPRINGDOC_VALUE);
        }
        // custom
        setPropIfNotExist(props, SipaBootConstant.Core.SIPA_APP_KEY, appName);
        setPropIfNotExist(props, SipaBootConstant.Core.SIPA_APP_VERSION_KEY, getVersion());
        setPropIfNotExist(props, SipaBootConstant.Core.SIPA_ENV_KEY, profile);
    }

    private static String getVersion() {
        String version = SystemUtil.get(SipaBootConstant.Core.SIPA_APP_VERSION_ENV_KEY, StrUtil.EMPTY);
        if (StrUtil.isNotBlank(version)) {
            String[] split = version.split(SipaConstant.Symbol.COLON);
            version = split[split.length - 1];
        } else {
            version = SipaBootConstant.Core.UNKNOWN;
        }
        return version;
    }

    private static void setPropForce(Properties props, String key, String defaultVal) {
        props.setProperty(key, defaultVal);
    }

    private static void setPropIfNotExist(Properties props, String key, String defaultVal) {
        String property = props.getProperty(key);
        if (Objects.isNull(property) || !StringUtils.hasLength(property)) {
            setPropForce(props, key, defaultVal);
        }
    }
}

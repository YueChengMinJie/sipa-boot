package com.sipa.boot.core.env;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import cn.hutool.core.lang.Tuple;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2022-09-14
 */
@Slf4j
public abstract class EnvProcessor {
    public static final String COMMON = "common";

    public static final String META_INF_PATH = "classpath:/META-INF/";

    public static final String PROPERTIES_FORMAT = "properties";

    public static final String SKIP_ENV = "SkipEnv#";

    public void process(ConfigurableEnvironment environment) throws Exception {
        String name = this.getName();
        String env = this.getEnv();

        // Nacos修改配置会导致Spring Environment和Context全部重新刷新，导致process又会重新执行一遍
        Boolean initialized = EnvCache.getEnvInitializationCache().get(name);
        if (initialized != null && initialized) {
            return;
        }
        EnvCache.getEnvInitializationCache().put(name, Boolean.TRUE);

        boolean isEnvLogShown = this.isEnvLogShown();
        if (isEnvLogShown) {
            log.info("Initialize {} env...", name);
        }
        this.processEnvProperties(environment, name, COMMON, true);
        this.processEnvProperties(environment, name, env, false);
    }

    public void processEnvProperties(ConfigurableEnvironment environment, String name, String env, boolean canMissing)
        throws IOException {
        String path = META_INF_PATH + name + "-" + env + "." + PROPERTIES_FORMAT;
        try {
            this.processProperties(environment, path);
        } catch (IOException e) {
            boolean isEnvLogShown = this.isEnvLogShown();
            if (canMissing) {
                if (isEnvLogShown) {
                    log.warn("* Not found {}, ignore to process...", path);
                } else {
                    log.info("* Not found " + path + ", ignore to process...");
                }
            } else {
                if (isEnvLogShown) {
                    log.error("* Not found {}, failed to process...", path);
                } else {
                    log.info("* Not found " + path + ", failed to process...");
                }
                throw e;
            }
        }
    }

    public void processProperties(ConfigurableEnvironment environment, String path) throws IOException {
        Properties properties = this.processProperties(path);
        for (String key : properties.stringPropertyNames()) {
            // 如果已经设置，则使用已经设置的值
            if (environment.getProperty(key) == null && System.getProperty(key) == null
                && System.getenv(key.toUpperCase()) == null) {
                String value = properties.getProperty(key);

                Tuple val = this.processValue(value);
                value = val.get(1);
                if (val.get(0)) {
                    // System.setProperty会触发Spring Environment配置值中的占位符被真实数据替换掉，但System Property配置值中的占位符依旧存在
                    System.setProperty(key, value);
                } else {
                    // System.setProperty会触发Spring Environment配置值中的占位符被真实数据替换掉，但System Property配置值中的占位符依旧存在
                    System.setProperty(key, value);

                    // System Property配置值中的占位符被真实数据替换掉，保证某些中间件（例如，Apollo）通过System.getProperty拿到最终值
                    String environmentValue = environment.getProperty(key);
                    if (StringUtils.isNotBlank(environmentValue)) {
                        System.setProperty(key, environmentValue);
                    }
                }
            }
        }
    }

    public Properties processProperties(String path) throws IOException {
        Properties properties = new Properties();

        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;

        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(path);
        try {
            inputStream = resource.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);

            properties.load(inputStreamReader);
        } finally {
            IOUtils.closeQuietly(inputStreamReader);
            IOUtils.closeQuietly(inputStream);
        }
        return properties;
    }

    /**
     * 提供扩展点，供实现端实现配置值的再加工
     */
    protected Tuple processValue(String value) {
        return new Tuple(value.startsWith(SKIP_ENV), StrUtil.removePrefix(value, SKIP_ENV));
    }

    protected boolean isEnvLogShown() {
        return true;
    }

    public String getDomain() {
        return EnvProvider.getDomain();
    }

    public String getRegion() {
        return EnvProvider.getRegion();
    }

    public String getEnv() {
        return EnvProvider.getEnv();
    }

    public String getServerPropertiesPath() {
        return EnvProvider.getServerPropertiesPath();
    }

    public abstract String getName();
}

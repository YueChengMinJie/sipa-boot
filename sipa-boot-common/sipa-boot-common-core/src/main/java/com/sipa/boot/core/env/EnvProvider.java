package com.sipa.boot.core.env;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2022-09-14
 */
@Slf4j
public class EnvProvider {
    public static final String DOMAIN_NAME = "sipa.domain";

    public static final String REGION_NAME = "sipa.region";

    public static final String ENV_NAME = "sipa.env";

    @Getter
    private static String domain;

    @Getter
    private static String region;

    @Getter
    private static String env;

    static {
        initializeDomain();
        initializeRegion();
        initializeEnv();
    }

    private static void initializeDomain() {
        try {
            domain = initializeContext(EnvConstant.DOMAIN_NAME);
        } catch (Exception e) {
            log.info("Initialize domain failed, use domain={} as default", EnvConstant.DOMAIN_VALUE);
        }
        if (StringUtils.isBlank(domain)) {
            // 通过三种方式之一未找到根域值，默认取预定义的静态变量值
            domain = EnvConstant.DOMAIN_VALUE;
        }
        // 设置到System Property，保证占位符生效。根域名值不可空缺的
        System.setProperty(EnvConstant.DOMAIN_NAME, domain);

        // 设置到System Property，提供给注册中心元数据用
        System.setProperty(DOMAIN_NAME, domain);
    }

    private static void initializeRegion() {
        try {
            region = initializeContext(EnvConstant.REGION_NAME);
        } catch (Exception e) {
            log.info("Initialize region failed, use [{}] as default", EnvConstant.DEFAULT_REGION);
        }
        if (StringUtils.isBlank(region)) {
            region = EnvConstant.DEFAULT_REGION;
            // 通过三种方式之一未找到区域，把空字符串设置到System Property，代替到占位符
            System.setProperty(EnvConstant.REGION_NAME, EnvConstant.DEFAULT_REGION);
        } else {
            // 通过三种方式之一找到区域，进行分隔符拼接，并设置到System Property，保证占位符生效
            // 前缀方式，把分隔符放在region前面；后缀方式，把分隔符放在region后面
            if (EnvConstant.REGION_SEPARATE_PREFIX) {
                System.setProperty(EnvConstant.REGION_NAME, EnvConstant.REGION_SEPARATE + region);
            } else {
                System.setProperty(EnvConstant.REGION_NAME, region + EnvConstant.REGION_SEPARATE);
            }
            // 设置到System Property，提供给注册中心元数据用
            System.setProperty(REGION_NAME, region);
        }
    }

    private static void initializeEnv() {
        try {
            env = initializeContext(EnvConstant.ENV_NAME);
        } catch (Exception e) {
            log.info("Initialize env failed, use env [{}] as default", EnvConstant.ENV_LOCAL);
        }
        if (StringUtils.isBlank(env)) {
            // 通过三种方式之一未找到环境值，默认取dev
            env = EnvConstant.ENV_LOCAL;
        }
        // 设置到System Property，提供给注册中心元数据用
        System.setProperty(ENV_NAME, env);
    }

    private static String initializeContext(String key) throws Exception {
        String value = System.getProperty(key);
        if (!StringUtils.isBlank(value)) {
            value = value.trim();
        } else {
            value = System.getenv(key.toUpperCase());
            if (!StringUtils.isBlank(value)) {
                value = value.trim();
            } else {
                Properties properties = new Properties();
                String path = getServerPropertiesPath();

                File file = new File(path);
                if (file.exists() && file.canRead()) {
                    FileInputStream inputStream = null;
                    InputStreamReader inputStreamReader = null;
                    try {
                        inputStream = new FileInputStream(file);
                        inputStreamReader = new InputStreamReader(inputStream);

                        properties.load(inputStreamReader);
                    } finally {
                        IOUtils.closeQuietly(inputStreamReader);
                        IOUtils.closeQuietly(inputStream);
                    }
                }
                value = properties.getProperty(key);
                if (!StringUtils.isBlank(value)) {
                    value = value.trim();
                }
            }
        }
        return value;
    }

    public static String getServerPropertiesPath() {
        return SystemUtils.IS_OS_WINDOWS ? EnvConstant.SERVER_PROPERTIES_PATH_WINDOWS
            : EnvConstant.SERVER_PROPERTIES_PATH_LINUX;
    }
}

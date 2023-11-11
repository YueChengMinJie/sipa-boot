package com.sipa.boot.core.env;

/**
 * @author caszhou
 * @date 2022-09-14
 */
public interface EnvConstant {
    /**
     * windows server.properties文件的存放位置
     */
    String SERVER_PROPERTIES_PATH_WINDOWS = "C:/opt/settings/server.properties";

    /**
     * unix server.properties文件的存放位置
     */
    String SERVER_PROPERTIES_PATH_LINUX = "/opt/settings/server.properties";

    /**
     * 根域名相关定义。包含三种传值方式，优先级至上而下。使用者需要把根域值改掉 <br/>
     * 1. 通过-Ddomain=sz-hm.cn或者System.setProperty("domain", "sz-hm.cn")方式进行传入 <br/>
     * 2. 通过大写的DOMAIN，其值为sz-hm.cn的System ENV方式进行传入 <br/>
     * 3. 通过gitops在server.properties定义domain=sz-hm.cn方式进行传入 <br/>
     */
    String DOMAIN_NAME = "domain";

    String DOMAIN_VALUE = "sz-hm.cn";

    /**
     * 区域名相关定义。包含三种传值方式，优先级至上而下 <br/>
     * 1. 通过-Dregion=sh-pd或者System.setProperty("region", "sh-pd")方式进行传入 <br/>
     * 2. 通过大写的REGION，其值为sh-pd的System ENV方式进行传入 <br/>
     * 3. 通过gitops在server.properties定义region=sh-pd方式进行传入 <br/>
     * <br />
     * 区域名分隔符相关定义 <br/>
     * REGION_SEPARATE表示区域在域名中的分隔符 <br/>
     * REGION_SEPARATE_PREFIX表示区域在域名中的分隔符是否在前面还是后面 <br/>
     * 包含两种表现形式。特别注意：region占位符前后切记不要出现分隔符，因为框架会自动去适配 <br/>
     * 1. 例如，原始格式为nacos-pro${region}.${domain} <br/>
     * 1.1 在region存在的情况下，会解析成nacos-prod-sh-pd.sz-hm.cn <br/>
     * 1.2 在region缺失的情况下，会解析成nacos-prod.sz-hm.cn <br/>
     * <br />
     * 2. 例如，原始格式为${region}prod-nacos.${domain} <br/>
     * 2.1 在region存在的情况下，会解析成sh-pd-prod-nacos.sz-hm.cn <br/>
     * 2.2 在region缺失的情况下，会解析成pro-nacos.sz-hm.cn <br/>
     */
    String REGION_NAME = "region";

    String REGION_SEPARATE = "-";

    boolean REGION_SEPARATE_PREFIX = true;

    /**
     * 默认区域
     */
    String DEFAULT_REGION = "sh-b";

    /**
     * 环境名相关定义。包含三种传值方式，优先级至上而下。以开发环境为例 <br/>
     * 1. 通过-Denv=dev或者System.setProperty("env", "dev")方式进行传入 <br/>
     * 2. 通过大写的ENV，其值为dev的System ENV方式进行传入 <br/>
     * 3. 通过gitops在server.properties定义env=dev方式进行传入 <br/>
     */
    String ENV_NAME = "env";

    /**
     * 本地环境
     */
    String ENV_LOCAL = "local";

    /**
     * 非本地环境
     */
    String NOT_ENV_LOCAL = "!local";

    /**
     * 开发环境
     */
    String ENV_DEV = "dev";

    /**
     * 测试环境
     */
    String ENV_FAT = "fat";

    /**
     * 生产环境
     */
    String ENV_PROD = "prod";

    /**
     * host name
     */
    String HOSTNAME_NAME = "hostname";

    /**
     * host name
     */
    String CANARY_NAME = "canary";
}

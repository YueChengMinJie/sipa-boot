package com.sipa.boot.core.constant;

/**
 * @author caszhou
 * @date 2023/6/17
 */
public interface TcpCloudConstant {
    String TCP_CLOUD_PREFIX = "sipa-boot.";

    String BCP_PREFIX = "hm-bcp-";

    interface Cache {
        String PREFIX = TCP_CLOUD_PREFIX + "cache";

        String ENABLED_KEY = "enabled";

        String ENABLED_VALUE = "true";
    }

    interface Core {
        String LIQUIBASE_KEY = "liquibase.shouldRun";

        String LIQUIBASE_VALUE = "false";

        String SOFA_ARK_HOST_KEY = "sofa.ark.embed.enable";

        String SOFA_ARK_HOST_VALUE = "true";

        String NETTY_KEY = "io.netty.tryReflectionSetAccessible";

        String NETTY_VALUE = "true";

        String SPRING_JMX_DEFAULT_DOMAIN_KEY = "spring.jmx.default-domain";

        String SPRING_APPLICATION_NAME_KEY = "spring.application.name";

        String SPRING_PROFILES_ACTIVE_KEY = "spring.profiles.active";

        String ALLOW_BEAN_DEFINITION_OVERRIDING_KEY = "spring.main.allow-bean-definition-overriding";

        String ALLOW_BEAN_DEFINITION_OVERRIDING_VALUE = "true";

        String ALLOW_CIRCULAR_REFERENCES_KEY = "spring.main.allow-circular-references";

        String ALLOW_CIRCULAR_REFERENCES_VALUE = "true";

        String SPRINGDOC_KEY = "springdoc.swagger-ui.enabled";

        String SPRINGDOC_VALUE = "false";

        String SIPA_NACOS_PREFIX = "sipa.";

        String SIPA_APP_KEY = "sipa.app";

        String SIPA_APP_VERSION_KEY = "sipa.app.version";

        String SIPA_APP_VERSION_ENV_KEY = "GITOPS_IMAGE_NAME";

        String UNKNOWN = "unknown";

        String SIPA_ENV_KEY = "sipa.env";
    }

    interface Feign {
        String PREFIX = TCP_CLOUD_PREFIX + "feign";

        String ENABLED_KEY = "enabled";

        String ENABLED_VALUE = "true";
    }

    interface Es {
        String PREFIX = TCP_CLOUD_PREFIX + "es";
    }

    interface Geetest {
        String PREFIX = TCP_CLOUD_PREFIX + "geetest";
    }

    interface Lock {
        String PREFIX = TCP_CLOUD_PREFIX + "lock";
    }

    interface OpenApi {
        String PREFIX = TCP_CLOUD_PREFIX + "openapi";
    }

    interface ArkHost {
        String PREFIX = TCP_CLOUD_PREFIX + "ark-host";
    }

    interface Storage {
        String PREFIX = TCP_CLOUD_PREFIX + "storage";
    }

    interface Xxljob {
        String PREFIX = TCP_CLOUD_PREFIX + "xxljob";
    }

    interface Gateway {
        String PREFIX = BCP_PREFIX + "gateway";

        String SECURE = PREFIX + SipaConstant.Symbol.POINT + "secure";

        String PATTERN = PREFIX + SipaConstant.Symbol.POINT + "pattern";
    }

    interface SecureServer {
        String PREFIX = TCP_CLOUD_PREFIX + "secure-server";

        String PATTERN = PREFIX + SipaConstant.Symbol.POINT + "pattern";
    }

    interface Rest {
        String KEY = TCP_CLOUD_PREFIX + "rest";
    }
}

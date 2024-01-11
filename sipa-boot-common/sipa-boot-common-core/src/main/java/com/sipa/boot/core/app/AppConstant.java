package com.sipa.boot.core.app;

/**
 * todo by caszhou 业务配置应该与框架解耦
 *
 * @author caszhou
 * @date 2023/3/3
 */
public interface AppConstant {
    String APP_SEATA = "seata";

    interface BCP {
        String APPLICATION_GATEWAY_NAME = "application-gateway-server";

        String IOT_NAME = "iot-service-server";

        String IOT_GATEWAY_NAME = "iot-gateway-service-server";

        String IOT_STATION_NAME = "iot-station-service-server";

        String SSO_IDP_NAME = "sso-idp-server";
    }

    interface TCP {
        String SIPA_BOOT_TEST_NAME = "sipa-boot-test";

        String CREATOR_NAME = "creator";
    }

    interface DCP {
        String LIQUIBASE_NAME = "liquibase-web-server";
    }

    interface FP {}

    interface BP {
        String AMS_NAME = "ams-service-server";
    }

    interface APPLICATION_ID {
        // 前台
        String FP_TODO = "1000001";

        // 中台
        String CP_SSO = "1000101";

        // 后台
        String BP_AMS = "1000201";
    }

    interface APPLICATION_ID_LONG {
        // 前台
        long FP_TODO = 1000001L;

        // 中台
        long CP_SSO = 1000101L;

        // 后台
        long BP_AMS = 1000201L;
    }
}

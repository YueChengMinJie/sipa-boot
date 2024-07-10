package com.sipa.boot.test;

import com.sipa.boot.core.app.AppConstant;
import com.sipa.boot.core.app.SipaApplication4Java;
import com.sipa.boot.core.constant.SipaBootConstant;
import com.sipa.boot.core.env.EnvConstant;
import com.sipa.boot.testcontainer.TestContainer;

/**
 * @author caszhou
 * @date 2023/5/29
 */
public class SipaTestContainer2 {
    public static void main(String[] args) {
        System.setProperty(SipaBootConstant.Core.SPRING_PROFILES_ACTIVE_KEY, EnvConstant.ENV_DEV);
        SipaApplication4Java.run(AppConstant.TCP.SIPA_BOOT_TEST_NAME, SipaTestApplication.class, args);
        TestContainer.start();
    }
}

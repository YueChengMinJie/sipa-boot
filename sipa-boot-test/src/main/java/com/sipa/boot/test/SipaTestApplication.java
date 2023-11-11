package com.sipa.boot.test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

import com.sipa.boot.core.app.AppConstant;
import com.sipa.boot.core.app.SipaApplication4Java;
import com.sipa.boot.test.rmq.context.SinkContext;
import com.sipa.boot.test.rmq.context.SourceContext;

/**
 * @author caszhou
 * @date 2023/5/29
 */
@SpringBootApplication
@EnableBinding({SourceContext.class, SinkContext.class})
public class SipaTestApplication {
    public static void main(String[] args) {
        SipaApplication4Java.run(AppConstant.TCP.SIPA_BOOT_TEST_NAME, SipaTestApplication.class, args);
    }
}

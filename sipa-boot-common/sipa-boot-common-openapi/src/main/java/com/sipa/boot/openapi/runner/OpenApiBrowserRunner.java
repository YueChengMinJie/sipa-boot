package com.sipa.boot.openapi.runner;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.sipa.boot.core.env.EnvConstant;
import com.sipa.boot.core.util.SipaUtil;

import cn.hutool.system.SystemUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2023/4/24
 */
@Slf4j
@Component
@Profile(EnvConstant.ENV_LOCAL)
public class OpenApiBrowserRunner implements ApplicationRunner {
    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${server.port}")
    private String port;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!SipaUtil.isBiz()) {
            String url = "http://127.0.0.1:" + this.contextPath + this.port + "/swagger-ui.html";
            if (SystemUtil.getOsInfo().isWindows()) {
                try {
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
                } catch (IOException e) {
                    log.error("Open swagger-ui at windows browser fail", e);
                }
            } else if (SystemUtil.getOsInfo().isMacOsX()) {
                try {
                    Class.forName("com.apple.eio.FileManager")
                        .getDeclaredMethod("openURL", String.class)
                        .invoke(null, url);
                } catch (Exception e) {
                    log.error("Open swagger-ui at mac browser fail", e);
                }
            }
        }
    }
}

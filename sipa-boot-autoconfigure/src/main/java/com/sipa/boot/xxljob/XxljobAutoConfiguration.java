package com.sipa.boot.xxljob;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.core.env.EnvConstant;
import com.sipa.boot.core.env.EnvProvider;
import com.sipa.boot.xxljob.property.XxljobProperty;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Configuration
@ConditionalOnClass(XxljobProperty.class)
@EnableConfigurationProperties(XxljobProperty.class)
public class XxljobAutoConfiguration {
    @Value("${spring.application.name}")
    private String name;

    @Value("${server.port}")
    private Integer port;

    @Value("${spring.profiles.active:local}")
    private String profile;

    private final XxljobProperty xxljobProperty;

    public XxljobAutoConfiguration(XxljobProperty xxljobProperty) {
        this.xxljobProperty = xxljobProperty;
    }

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor(InetUtils inetUtils) {
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(this.xxljobProperty.getAdminAddresses());
        xxlJobSpringExecutor.setAppname(this.getAppName());
        xxlJobSpringExecutor.setIp(inetUtils.findFirstNonLoopbackHostInfo().getIpAddress());
        xxlJobSpringExecutor.setPort(this.port + 1000);
        xxlJobSpringExecutor.setAccessToken(this.xxljobProperty.getAccessToken());
        String env = EnvProvider.getEnv();
        if (EnvConstant.ENV_LOCAL.equals(env)) {
            xxlJobSpringExecutor.setLogPath("./logs/" + this.name + "/xxl-job/jobhandler");
        } else {
            xxlJobSpringExecutor.setLogPath("/logs/" + this.name + "/xxl-job/jobhandler");
        }
        xxlJobSpringExecutor.setLogRetentionDays(this.xxljobProperty.getLogRetentionDays());
        return xxlJobSpringExecutor;
    }

    private String getAppName() {
        String env = EnvProvider.getEnv();
        if (EnvConstant.ENV_PROD.equals(env)) {
            return this.name;
        } else {
            return this.name + SipaConstant.Symbol.ACROSS + env;
        }
    }
}

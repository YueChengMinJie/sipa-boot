package com.sipa.boot.secure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.sipa.boot.core.constant.SipaConstant;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.log.SaLog;
import cn.dev33.satoken.stp.StpLogic;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Configuration
@ConditionalOnClass(SaLogForSlf4j.class)
public class SecureAutoConfiguration {
    @Value("${sipa-boot.secure.jwt-secret-key:12j312j3k123ljkljasklda12489789}")
    private String jwtSecretKey;

    @Value("${sipa-boot.secure.concurrent:false}")
    private boolean concurrent;

    @Bean
    @Primary
    public SaTokenConfig getSaTokenConfigPrimary() {
        SaTokenConfig config = new SaTokenConfig();
        config.setTokenName(SipaConstant.AUTH_KEY);
        config.setTimeout(4 * 24 * 60 * 60);
        config.setActivityTimeout(4 * 24 * 60 * 60);
        config.setIsConcurrent(this.concurrent);
        config.setIsLog(true);
        config.setTokenPrefix(SipaConstant.TOKEN_PREFIX);
        config.setIsReadCookie(false);
        config.setIsPrint(false);
        config.setJwtSecretKey(this.jwtSecretKey);
        return config;
    }

    @Bean
    public StpLogic stpLogicJwtForSimple() {
        return new StpLogicJwtForSimple();
    }

    @Bean
    public SaLog saLogForSlf4j() {
        return new SaLogForSlf4j();
    }
}

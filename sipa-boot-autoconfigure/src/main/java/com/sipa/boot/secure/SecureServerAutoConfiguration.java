package com.sipa.boot.secure;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpHeaders;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;

import com.sipa.boot.core.allinone.SipaRequest;
import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.core.env.EnvConstant;
import com.sipa.boot.secure.server.IdpUserHoldFilter;
import com.sipa.boot.secure.server.IdpUserInterceptor;
import com.sipa.boot.secure.server.SameTokenInterceptor;
import com.sipa.boot.secure.server.payload.UserInfoInjectorAspect;
import com.sipa.boot.secure.server.property.SecureServerProperty;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.spring.SpringMVCUtil;
import cn.hutool.json.JSONUtil;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Configuration
@EnableAspectJAutoProxy(exposeProxy = true)
@ConditionalOnClass(SameTokenInterceptor.class)
public class SecureServerAutoConfiguration {
    @Bean
    public Filter idpUserHoldFilter() {
        return new IdpUserHoldFilter();
    }

    @Configuration
    @RequiredArgsConstructor
    @EnableConfigurationProperties(SecureServerProperty.class)
    public static class FeignSecureServerAutoConfiguration {
        private final SecureServerProperty secureServerProperty;

        @Bean
        @Profile(EnvConstant.NOT_ENV_LOCAL)
        public SaServletFilter saServletFilter() {
            String authPattern = this.secureServerProperty.getAuthPattern();
            String[] springDocPattern = this.secureServerProperty.getSpringDocPattern();

            return new SaServletFilter()

                .addInclude(authPattern)
                .addExclude(springDocPattern)

                .setAuth(obj -> {
                    String token = SaHolder.getRequest().getHeader(SaSameUtil.SAME_TOKEN);
                    SaSameUtil.checkToken(token);
                })

                .setError(e -> {
                    HttpServletResponse response = SpringMVCUtil.getResponse();
                    response.setStatus(SipaSecureUtil.getStatusCode(e));
                    response.setHeader(HttpHeaders.CONTENT_TYPE, SipaConstant.HttpHeaderValue.JSON_UTF8);
                    return JSONUtil.toJsonStr(SipaSecureUtil.handlerException(e));
                });
        }

        @Bean
        @Profile(EnvConstant.NOT_ENV_LOCAL)
        public RequestInterceptor sameTokenInterceptor() {
            return new SameTokenInterceptor();
        }

        @Bean
        public RequestInterceptor idpUserInterceptor() {
            return new IdpUserInterceptor();
        }

        @Bean
        @ConditionalOnClass(SipaRequest.class)
        public UserInfoInjectorAspect userInfoInjectorAspect() {
            return new UserInfoInjectorAspect();
        }
    }
}

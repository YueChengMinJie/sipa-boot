package com.sipa.boot.secure;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sipa.boot.secure.ssr.SsrPlaceholder;

import cn.dev33.satoken.basic.SaBasicUtil;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.router.SaRouter;
import lombok.SneakyThrows;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Configuration
@ConditionalOnClass(SsrPlaceholder.class)
public class SecureSsrAutoConfiguration {
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()

            .addInclude("/**")
            .addExclude("/favicon.ico")

            .setAuth(obj -> SaRouter.match("/**", () -> {
                try {
                    SaBasicUtil.check("admin:Aa123456");
                } catch (Exception e) {
                    return401();
                }
            }));
    }

    @SneakyThrows(IOException.class)
    private static void return401() {
        HttpServletResponse response =
            ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getResponse();
        Objects.requireNonNull(response).sendError(401);
    }
}

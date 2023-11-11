package com.sipa.boot.secure;

import java.util.Objects;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import com.sipa.boot.core.secure.IdpUser;
import com.sipa.boot.core.secure.IdpUserHolder;
import com.sipa.boot.secure.gateway.ForwardAuthFilter;
import com.sipa.boot.secure.gateway.IdpManager;
import com.sipa.boot.secure.gateway.StpInterfaceImpl;
import com.sipa.boot.secure.gateway.property.GatewayPatternProperty;
import com.sipa.boot.secure.gateway.property.GatewaySecureProperty;
import com.sipa.boot.secure.gateway.util.SipaStpUtil;

import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.router.SaRouterStaff;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnClass(GatewaySecureProperty.class)
@EnableConfigurationProperties({GatewaySecureProperty.class, GatewayPatternProperty.class})
public class SecureGatewayAutoConfiguration {
    private static final String LOGIN_TYPE = "login";

    private final GatewayPatternProperty patternProperty;

    private final GatewaySecureProperty secureProperty;

    @Bean
    public SaReactorFilter getSaReactorFilter() {
        String authPattern = this.patternProperty.getAuthPattern();
        String[] authExcludePattern = this.patternProperty.getAuthExcludePattern();
        String[] springDocPattern = this.patternProperty.getSpringDocPattern();

        return new SaReactorFilter()

            // 拦截路由
            .addInclude(authPattern)

            // 放行路由
            .addExclude(authExcludePattern)
            .addExclude(springDocPattern)

            // 鉴权
            .setAuth(
                obj -> SaRouter.match(authPattern).notMatch(authExcludePattern).notMatch(springDocPattern).check(r -> {
                    // 登陆校验
                    StpUtil.checkLogin();

                    // 从token中取user
                    IdpUser idpUser = SecureHelper.getCurrentIdpUser();

                    if (Objects.nonNull(idpUser) && Objects.nonNull(idpUser.getId())) {
                        // 外部用户应用校验 内部用户权限校验
                        if (Objects.equals(idpUser.getAccountType(), IdpUser.AccountType.EXTERNAL)) {
                            String[] externalAccountAllowedPathPattern =
                                this.secureProperty.getExternalAccountAllowedPathPattern();
                            SaRouterStaff match = r.match(externalAccountAllowedPathPattern);
                            if (!match.isHit) {
                                throw NotLoginException.newInstance(LOGIN_TYPE, NotLoginException.INVALID_TOKEN)
                                    .setCode(SaErrorCode.CODE_11012);
                            }
                        } else {
                            // 权限校验
                            SipaStpUtil.checkPermission(idpUser);
                        }

                        // 角色校验
                        // SipaStpUtil.checkRole();

                        // user放头中，传递到各个服务
                        SaReactorSyncHolder.getContext()
                            .getRequest()
                            .mutate()
                            .header(IdpUser.REST_WEB_KEY, SecureHelper.convert(idpUser));

                        // 临时放入给网关用
                        IdpUserHolder.set(idpUser);
                    }
                }))

            .setError(e -> {
                ServerWebExchange exchange = SaReactorSyncHolder.getContext();
                ServerHttpResponse response = exchange.getResponse();
                response.setRawStatusCode(SipaSecureUtil.getStatusCode(e));
                response.getHeaders().set("Content-Type", "application/json; charset=utf-8");
                return JSONUtil.toJsonStr(SipaSecureUtil.handlerException(e));
            })

            .setBeforeAuth(obj -> SaRouter.match(SaHttpMethod.OPTIONS).free(r -> log.info("OPTIONS预检请求，不做处理")).back());
    }

    @Bean
    public StpInterface stpInterface() {
        return new StpInterfaceImpl();
    }

    @Bean
    public IdpManager idpManager() {
        return new IdpManager();
    }

    @Bean
    public GlobalFilter forwardAuthFilter() {
        return new ForwardAuthFilter();
    }
}

package com.sipa.boot.gateway;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SwaggerUiConfigParameters;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import com.sipa.boot.core.app.AppConstant;
import com.sipa.boot.gateway.exception.GatewayErrorWebExceptionHandler;
import com.sipa.boot.gateway.filter.RequestLogFilter;

/**
 * @author caszhou
 * @date 2023/3/11
 */
@Configuration
@ConditionalOnWebApplication
@ComponentScan("com.sipa.boot.gateway.**")
@ConditionalOnClass(RequestLogFilter.class)
@AutoConfigureBefore(ErrorWebFluxAutoConfiguration.class)
public class GatewayAutoConfiguration {
    public static final String PREFIX = "sipa-boot.openapi";

    public static final String IS_GATEWAY_KEY = "mode";

    public static final String IS_GATEWAY_VALUE = "gateway";

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, value = IS_GATEWAY_KEY, havingValue = IS_GATEWAY_VALUE)
    public List<GroupedOpenApi> apis(SwaggerUiConfigParameters swaggerUiConfigParameters,
        RouteDefinitionLocator locator) {
        List<GroupedOpenApi> groups = new ArrayList<>();
        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();
        if (CollectionUtils.isNotEmpty(definitions)) {
            definitions.forEach(routeDefinition -> {
                String name = routeDefinition.getId();
                String[] names = name.split("_");
                if (names.length == 2) {
                    String sid = names[1];
                    if (!sid.startsWith(AppConstant.APP_SEATA) && !sid.startsWith(AppConstant.DCP.LIQUIBASE_NAME)) {
                        swaggerUiConfigParameters.addGroup(sid);
                        groups.add(GroupedOpenApi.builder().pathsToMatch("/" + sid + "/**").group(sid).build());
                    }
                }
            });
        }
        return groups;
    }

    @Bean
    public NettyServerCustomizer lunaNettyServerCustomizer() {
        return httpServer -> httpServer.idleTimeout(Duration.ofSeconds(60));
    }

    @Bean
    public WebProperties.Resources resources() {
        return new WebProperties.Resources();
    }

    @Bean
    @Order(-2)
    public ErrorWebExceptionHandler errorWebExceptionHandler(ErrorAttributes errorAttributes,
        ObjectProvider<ViewResolver> viewResolvers, ServerCodecConfigurer serverCodecConfigurer,
        ServerProperties serverProperties, ApplicationContext applicationContext) {
        DefaultErrorWebExceptionHandler exceptionHandler = new GatewayErrorWebExceptionHandler(errorAttributes,
            this.resources(), serverProperties.getError(), applicationContext);
        exceptionHandler.setViewResolvers(viewResolvers.orderedStream().collect(Collectors.toList()));
        exceptionHandler.setMessageWriters(serverCodecConfigurer.getWriters());
        exceptionHandler.setMessageReaders(serverCodecConfigurer.getReaders());
        return exceptionHandler;
    }
}

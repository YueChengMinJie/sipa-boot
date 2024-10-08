package com.sipa.boot.openapi;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.core.env.EnvConstant;
import com.sipa.boot.core.property.YamlPropertySourceFactory;
import com.sipa.boot.openapi.property.OpenApiProperty;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.SneakyThrows;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(OpenApiProperty.class)
@ComponentScan("com.sipa.boot.openapi.**")
@EnableConfigurationProperties(OpenApiProperty.class)
@PropertySource(value = "classpath:openapi3.yml", factory = YamlPropertySourceFactory.class)
public class OpenApiAutoConfiguration {
    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${spring.application.name}")
    private String name;

    @Value("${server.port}")
    private String port;

    @Value("${spring.profiles.active:local}")
    private String profile;

    private final OpenApiProperty openApiProperty;

    private final InetUtils inetUtils;

    public OpenApiAutoConfiguration(OpenApiProperty openApiProperty, InetUtils inetUtils) {
        this.openApiProperty = openApiProperty;
        this.inetUtils = inetUtils;
    }

    @Bean
    public OpenAPI openApi() {
        String url = "http://127.0.0.1:" + this.port + this.contextPath;
        return new OpenAPI()
            .components(new Components().addSecuritySchemes(SipaConstant.AUTH_KEY,
                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
            .info(new Info().title(this.name)
                .description(this.openApiProperty.getDesc())
                .version(this.openApiProperty.getVersion())
                // todo by caszhou 业务配置应该与框架解耦
                .license(new License().name("苏州玥呈敏洁网络科技有限公司").url("https://www.sipa.com/")))
            .externalDocs(new ExternalDocumentation()
                .description(StringUtils.trimToEmpty(this.openApiProperty.getDesc()) + "接口文档")
                .url(url + "/swagger-ui.html"))
            .servers(this.getServers(url));
    }

    @SneakyThrows
    private List<Server> getServers(String url) {
        if (StringUtils.equals(this.profile, EnvConstant.ENV_LOCAL)) {
            return List.of(
                // localhost
                new Server().url(url),
                // 本地网关
                new Server().url("http://localhost:8000/" + this.name + this.contextPath),
                // 内网网关
                new Server().url("http://" + inetUtils.findFirstNonLoopbackHostInfo().getIpAddress() + ":8000/"
                    + this.name + this.contextPath));
        } else {
            return List.of(
                // dev todo by caszhou 业务配置应该与框架解耦
                new Server().url("http://gateway-dev.sipa.com/" + this.name + this.contextPath),
                // fat todo by caszhou 业务配置应该与框架解耦
                new Server().url("http://gateway-fat.sipa.com/" + this.name + this.contextPath),
                // prod todo by caszhou 业务配置应该与框架解耦
                new Server().url("http://gateway.sipa.com/" + this.name + this.contextPath));
        }
    }
}

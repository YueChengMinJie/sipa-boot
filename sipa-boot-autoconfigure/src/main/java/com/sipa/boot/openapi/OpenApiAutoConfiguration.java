package com.sipa.boot.openapi;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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

    public OpenApiAutoConfiguration(OpenApiProperty openApiProperty) {
        this.openApiProperty = openApiProperty;
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
                .license(new License().name("苏州恒美电子科技股份有限公司").url("https://www.sz-hm.cn/")))
            .externalDocs(new ExternalDocumentation()
                .description(StringUtils.trimToEmpty(this.openApiProperty.getDesc()) + "接口文档")
                .url(url + "/swagger-ui.html"))
            .servers(this.getServers(url));
    }

    private List<Server> getServers(String url) {
        if (StringUtils.equals(this.profile, EnvConstant.ENV_LOCAL)) {
            return List.of(new Server().url(url),
                new Server().url("http://localhost:8000/" + this.name + this.contextPath));
        } else {
            return List.of(
                // dev todo by caszhou 业务配置应该与框架解耦
                new Server().url("http://gateway-dev.sz-hm.cn/" + this.name + this.contextPath),
                // fat todo by caszhou 业务配置应该与框架解耦
                new Server().url("http://gateway-fat.sz-hm.cn/" + this.name + this.contextPath),
                // prod todo by caszhou 业务配置应该与框架解耦
                new Server().url("http://gateway.sz-hm.cn/" + this.name + this.contextPath));
        }
    }
}

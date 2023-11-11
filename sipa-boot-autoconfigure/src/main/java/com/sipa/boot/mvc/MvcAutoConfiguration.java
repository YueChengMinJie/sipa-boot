package com.sipa.boot.mvc;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.core.enumeration.formatter.IEnumConverterFactory;
import com.sipa.boot.core.env.EnvConstant;
import com.sipa.boot.core.property.YamlPropertySourceFactory;
import com.sipa.boot.mvc.log.LogInterceptor;
import com.sipa.boot.mvc.response.NoPackage;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(NoPackage.class)
@ComponentScan(value = {"com.sipa.boot.mvc.**"})
@PropertySource(value = "classpath:mvc.yml", factory = YamlPropertySourceFactory.class)
public class MvcAutoConfiguration implements WebMvcConfigurer {
    @Value("${spring.profiles.active:local}")
    private String profile;

    private final ObjectMapper objectMapper;

    public MvcAutoConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void addFormatters(@Nonnull FormatterRegistry registry) {
        this.addDateTimeFormatter(registry);
        this.addIEnumConverterFactory(registry);
    }

    private void addIEnumConverterFactory(FormatterRegistry registry) {
        registry.addConverterFactory(new IEnumConverterFactory());
    }

    private void addDateTimeFormatter(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setUseIsoFormat(false);
        registrar.setDateFormatter(SipaConstant.Formatter.DATE_DEFAULT);
        registrar.setTimeFormatter(SipaConstant.Formatter.TIME_DEFAULT);
        registrar.setDateTimeFormatter(SipaConstant.Formatter.DATE_TIME_DEFAULT);
        registrar.registerFormatters(registry);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.removeIf(converter -> converter instanceof MappingJackson2HttpMessageConverter);
        converters.add(new MappingJackson2HttpMessageConverter(this.objectMapper));
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (StringUtils.equals(this.profile, EnvConstant.ENV_LOCAL)) {
            registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "TRACE")
                .allowedHeaders("*")
                .maxAge(600);
        }
    }

    @Bean
    public LogInterceptor logInterceptor() {
        return new LogInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.logInterceptor()).addPathPatterns("/**");
    }
}

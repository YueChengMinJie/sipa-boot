package com.sipa.boot.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import javax.validation.constraints.NotNull;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.sipa.boot.core.apm.ApmHandlerInterceptor;
import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.core.enumeration.deseriallzer.EnumDeserializer;
import com.sipa.boot.core.enumeration.serializer.EnumSerializer;
import com.sipa.boot.core.jackson.deserializer.MillisOrLocalDateTimeDeserializer;
import com.sipa.boot.core.jackson.serializer.LongSerializer;
import com.sipa.boot.core.property.YamlPropertySourceFactory;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * @author caszhou
 * @date 2019-01-22
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ComponentScan("com.sipa.boot.core.**")
@ConditionalOnClass(Jackson2ObjectMapperBuilder.class)
@PropertySource(value = "classpath:core.yml", factory = YamlPropertySourceFactory.class)
public class CoreAutoConfiguration {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder.serializationInclusion(JsonInclude.Include.NON_NULL)
            // 序列化反序列化
            .postConfigurer(objectMapper -> {
                objectMapper.configure(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS, Boolean.FALSE);
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.FALSE);
            })
            // 其它模块
            .modules(this.getSimpleModule(), this.getJavaTimeModule())
            .timeZone(TimeZone.getTimeZone("Asia/Shanghai"))
            .simpleDateFormat(SipaConstant.TimeFormatKey.DATE_TIME_DEFAULT)
            .featuresToEnable(MapperFeature.PROPAGATE_TRANSIENT_MARKER)
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private JavaTimeModule getJavaTimeModule() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(SipaConstant.TimeFormatKey.DATE_TIME_DEFAULT);
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new MillisOrLocalDateTimeDeserializer(dateTimeFormatter));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(SipaConstant.TimeFormatKey.DATE_DEFAULT);
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(SipaConstant.TimeFormatKey.TIME_DEFAULT);
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormatter));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(timeFormatter));

        return javaTimeModule;
    }

    @NotNull
    private SimpleModule getSimpleModule() {
        SimpleModule simpleModule = new SimpleModule();
        // enum
        simpleModule.addSerializer(Enum.class, EnumSerializer.INSTANCE);
        simpleModule.addDeserializer(Enum.class, EnumDeserializer.INSTANCE);
        // long
        simpleModule.addSerializer(Long.class, LongSerializer.INSTANCE);
        return simpleModule;
    }

    @Bean
    public MapperFacade mapperFacade() {
        return new DefaultMapperFactory.Builder().build().getMapperFacade();
    }

    @Configuration
    @ConditionalOnClass(WebMvcConfigurer.class)
    public static class MvcCoreAutoConfiguration implements WebMvcConfigurer {
        @Bean
        public ApmHandlerInterceptor apmHandlerInterceptor() {
            return new ApmHandlerInterceptor();
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(this.apmHandlerInterceptor()).addPathPatterns("/**");
        }
    }
}

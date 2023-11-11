package com.sipa.boot.feign;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.cloud.openfeign.FeignLoggerFactory;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.format.support.FormattingConversionService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipa.boot.core.constant.TcpCloudConstant;
import com.sipa.boot.core.property.YamlPropertySourceFactory;
import com.sipa.boot.feign.decoder.UnwrapDecoder;
import com.sipa.boot.feign.decoder.UnwrapDecoderPro;
import com.sipa.boot.feign.enumeration.UniversalReversedEnumConverter;
import com.sipa.boot.feign.header.HeaderRequestInterceptor;
import com.sipa.boot.feign.log.CustomFeignLoggerFactory;
import com.sipa.boot.feign.log.CustomSlf4jLogger;
import com.sipa.boot.feign.property.FeignProperty;

import feign.Contract;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.optionals.OptionalDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2023/2/18
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(FeignProperty.class)
@AutoConfigureBefore(FeignClientsConfiguration.class)
@ComponentScan(value = {"com.sipa.boot.feign.**"})
@ConditionalOnClass({FeignProperty.class, UnwrapDecoder.class})
@PropertySource(value = "classpath:feign.yml", factory = YamlPropertySourceFactory.class)
@EnableFeignClients(basePackages = {"com.hm", "com.hmev"}) // todo by caszhou 业务配置应该与框架解耦
@ConditionalOnProperty(prefix = TcpCloudConstant.Feign.PREFIX, value = TcpCloudConstant.Feign.ENABLED_KEY,
    havingValue = TcpCloudConstant.Feign.ENABLED_VALUE)
public class FeignAutoConfiguration {
    @Autowired
    private ObjectMapper objectMapper;

    @Resource
    private FormattingConversionService conversionService;

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    public Contract feignContract(List<AnnotatedParameterProcessor> parameterProcessors) {
        this.conversionService.addConverter(new UniversalReversedEnumConverter());
        return new SpringMvcContract(parameterProcessors, this.conversionService);
    }

    @Bean
    public Decoder decoder(ObjectProvider<HttpMessageConverterCustomizer> customizers) {
        return new UnwrapDecoderPro(this.objectMapper,
            new OptionalDecoder(new ResponseEntityDecoder(new SpringDecoder(this.messageConverters, customizers))));
    }

    @Bean
    public RequestInterceptor headerRequestInterceptor() {
        return new HeaderRequestInterceptor();
    }

    @Bean
    public FeignLoggerFactory feignLoggerFactory() {
        return new CustomFeignLoggerFactory(new CustomSlf4jLogger());
    }
}

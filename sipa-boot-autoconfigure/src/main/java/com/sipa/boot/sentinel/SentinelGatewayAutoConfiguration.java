package com.sipa.boot.sentinel;

import static com.alibaba.cloud.sentinel.SentinelConstants.BLOCK_PAGE_URL_CONF_KEY;
import static com.alibaba.csp.sentinel.config.SentinelConfig.setConfig;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.cloud.sentinel.SentinelProperties;
import com.alibaba.cloud.sentinel.custom.SentinelDataSourceHandler;
import com.alibaba.cloud.sentinel.datasource.converter.JsonConverter;
import com.alibaba.cloud.sentinel.gateway.ConfigConstants;
import com.alibaba.cloud.sentinel.gateway.scg.SentinelSCGAutoConfiguration;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.init.InitExecutor;
import com.alibaba.csp.sentinel.log.LogBase;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.transport.config.TransportConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.sipa.boot.sentinel.gateway.ApiPredicateItemDeserializer;
import com.sipa.boot.sentinel.gateway.SentinelGatewayEnvProcessor;
import com.sipa.boot.sentinel.gateway.SipaBlockRequestHandler;

/**
 * @author caszhou
 * @date 2024/12/20
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(SentinelSCGAutoConfiguration.class)
@ConditionalOnClass(SentinelGatewayEnvProcessor.class)
@EnableConfigurationProperties(SentinelProperties.class)
public class SentinelGatewayAutoConfiguration {
    @Value("${project.name:${spring.application.name:}}")
    private String projectName;

    private final SentinelProperties properties;

    public SentinelGatewayAutoConfiguration(SentinelProperties properties) {
        this.properties = properties;
    }

    @Bean
    public BlockRequestHandler sipaBlockRequestHandler() {
        return new SipaBlockRequestHandler();
    }

    @PostConstruct
    private void init() {
        if (StringUtils.isEmpty(System.getProperty(LogBase.LOG_DIR))
            && StringUtils.isNotBlank(properties.getLog().getDir())) {
            System.setProperty(LogBase.LOG_DIR, properties.getLog().getDir());
        }
        if (StringUtils.isEmpty(System.getProperty(LogBase.LOG_NAME_USE_PID)) && properties.getLog().isSwitchPid()) {
            System.setProperty(LogBase.LOG_NAME_USE_PID, String.valueOf(properties.getLog().isSwitchPid()));
        }
        if (StringUtils.isEmpty(System.getProperty(SentinelConfig.APP_NAME_PROP_KEY))
            && StringUtils.isNotBlank(projectName)) {
            System.setProperty(SentinelConfig.APP_NAME_PROP_KEY, projectName);
        }
        if (StringUtils.isEmpty(System.getProperty(TransportConfig.SERVER_PORT))
            && StringUtils.isNotBlank(properties.getTransport().getPort())) {
            System.setProperty(TransportConfig.SERVER_PORT, properties.getTransport().getPort());
        }
        if (StringUtils.isEmpty(System.getProperty(TransportConfig.CONSOLE_SERVER))
            && StringUtils.isNotBlank(properties.getTransport().getDashboard())) {
            System.setProperty(TransportConfig.CONSOLE_SERVER, properties.getTransport().getDashboard());
        }
        if (StringUtils.isEmpty(System.getProperty(TransportConfig.HEARTBEAT_INTERVAL_MS))
            && StringUtils.isNotBlank(properties.getTransport().getHeartbeatIntervalMs())) {
            System.setProperty(TransportConfig.HEARTBEAT_INTERVAL_MS,
                properties.getTransport().getHeartbeatIntervalMs());
        }
        if (StringUtils.isEmpty(System.getProperty(TransportConfig.HEARTBEAT_CLIENT_IP))
            && StringUtils.isNotBlank(properties.getTransport().getClientIp())) {
            System.setProperty(TransportConfig.HEARTBEAT_CLIENT_IP, properties.getTransport().getClientIp());
        }
        if (StringUtils.isEmpty(System.getProperty(SentinelConfig.CHARSET))
            && StringUtils.isNotBlank(properties.getMetric().getCharset())) {
            System.setProperty(SentinelConfig.CHARSET, properties.getMetric().getCharset());
        }
        if (StringUtils.isEmpty(System.getProperty(SentinelConfig.SINGLE_METRIC_FILE_SIZE))
            && StringUtils.isNotBlank(properties.getMetric().getFileSingleSize())) {
            System.setProperty(SentinelConfig.SINGLE_METRIC_FILE_SIZE, properties.getMetric().getFileSingleSize());
        }
        if (StringUtils.isEmpty(System.getProperty(SentinelConfig.TOTAL_METRIC_FILE_COUNT))
            && StringUtils.isNotBlank(properties.getMetric().getFileTotalCount())) {
            System.setProperty(SentinelConfig.TOTAL_METRIC_FILE_COUNT, properties.getMetric().getFileTotalCount());
        }
        if (StringUtils.isEmpty(System.getProperty(SentinelConfig.COLD_FACTOR))
            && StringUtils.isNotBlank(properties.getFlow().getColdFactor())) {
            System.setProperty(SentinelConfig.COLD_FACTOR, properties.getFlow().getColdFactor());
        }
        if (StringUtils.isNotBlank(properties.getBlockPage())) {
            setConfig(BLOCK_PAGE_URL_CONF_KEY, properties.getBlockPage());
        }

        // earlier initialize
        if (properties.isEager()) {
            System.setProperty(SentinelConfig.APP_TYPE_PROP_KEY, ConfigConstants.APP_TYPE_SCG_GATEWAY);
            InitExecutor.doInit();
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public SentinelDataSourceHandler sentinelDataSourceHandler(DefaultListableBeanFactory beanFactory,
        SentinelProperties sentinelProperties, Environment env) {
        return new SentinelDataSourceHandler(beanFactory, sentinelProperties, env);
    }

    @ConditionalOnClass(ObjectMapper.class)
    @Configuration(proxyBeanMethods = false)
    protected static class SentinelConverterConfiguration {
        @Configuration(proxyBeanMethods = false)
        protected static class SentinelJsonConfiguration {
            private ObjectMapper objectMapper = new ObjectMapper();

            public SentinelJsonConfiguration() {
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                SimpleModule module = new SimpleModule();
                module.addDeserializer(ApiPredicateItem.class, new ApiPredicateItemDeserializer());
                objectMapper.registerModule(module);
            }

            @Bean("sentinel-json-gw-flow-converter")
            public JsonConverter jsonGwFlowConverter() {
                return new JsonConverter(objectMapper, GatewayFlowRule.class);
            }

            @Bean("sentinel-json-gw-api-group-converter")
            public JsonConverter jsonGwApiGroupConverter() {
                return new JsonConverter(objectMapper, ApiDefinition.class);
            }

            @Bean("sentinel-json-degrade-converter")
            public JsonConverter jsonDegradeConverter() {
                return new JsonConverter(objectMapper, DegradeRule.class);
            }

            @Bean("sentinel-json-system-converter")
            public JsonConverter jsonSystemConverter() {
                return new JsonConverter(objectMapper, SystemRule.class);
            }
        }
    }
}

package com.alibaba.csp.sentinel.dashboard.rule.nacos.gateway;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.alibaba.csp.sentinel.dashboard.common.SentinelNacosConstants;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.ApiDefinitionEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author caszhou
 * @date 2024/12/17
 */
@Component
public class NacosGwApiGroupRuleApiPublisher implements DynamicRulePublisher<List<ApiDefinitionEntity>> {
    private final ConfigService configService;

    private final ObjectMapper objectMapper;

    public NacosGwApiGroupRuleApiPublisher(ConfigService configService, ObjectMapper objectMapper) {
        this.configService = configService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(String app, List<ApiDefinitionEntity> rules) throws Exception {
        configService.publishConfig(app + SentinelNacosConstants.GW_API_GROUP_POSTFIX, SentinelNacosConstants.GROUP_ID,
            this.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rules), ConfigType.JSON.getType());
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
    }
}

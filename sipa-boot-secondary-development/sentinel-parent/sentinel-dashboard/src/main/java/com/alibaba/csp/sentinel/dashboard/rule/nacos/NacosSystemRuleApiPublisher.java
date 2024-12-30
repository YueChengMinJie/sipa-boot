package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.alibaba.csp.sentinel.dashboard.common.SentinelNacosConstants;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.SystemRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author caszhou
 * @date 2024/12/17
 */
@Component
public class NacosSystemRuleApiPublisher implements DynamicRulePublisher<List<SystemRuleEntity>> {
    private final ConfigService configService;

    private final ObjectMapper objectMapper;

    public NacosSystemRuleApiPublisher(ConfigService configService, ObjectMapper objectMapper) {
        this.configService = configService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(String app, List<SystemRuleEntity> rules) throws Exception {
        configService.publishConfig(app + SentinelNacosConstants.SYSTEM_DATA_ID_POSTFIX,
            SentinelNacosConstants.GROUP_ID,
            this.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rules), ConfigType.JSON.getType());
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
    }
}

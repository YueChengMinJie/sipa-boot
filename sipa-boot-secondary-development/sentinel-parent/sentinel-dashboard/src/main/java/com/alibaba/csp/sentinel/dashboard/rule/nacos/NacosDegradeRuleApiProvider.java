package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.alibaba.csp.sentinel.dashboard.common.SentinelNacosConstants;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.nacos.api.config.ConfigService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author caszhou
 * @date 2024/12/17
 */
@Component
public class NacosDegradeRuleApiProvider implements DynamicRuleProvider<List<DegradeRuleEntity>> {
    private final ConfigService configService;

    private final ObjectMapper objectMapper;

    public NacosDegradeRuleApiProvider(ConfigService configService, ObjectMapper objectMapper) {
        this.configService = configService;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<DegradeRuleEntity> getRules(String appName) throws Exception {
        String config = configService.getConfig(appName + SentinelNacosConstants.DEGRADE_DATA_ID_POSTFIX,
            SentinelNacosConstants.GROUP_ID, 5000L);
        if (config == null) {
            return Collections.emptyList();
        }
        return this.objectMapper.readValue(config, new TypeReference<List<DegradeRuleEntity>>() {});
    }
}

package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import java.util.List;

import org.springframework.stereotype.Component;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.SystemRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.util.NacosUtils;
import com.alibaba.nacos.api.config.ConfigService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author caszhou
 * @date 2024/12/17
 */
@Component
public class NacosSystemRuleApiProvider implements DynamicRuleProvider<List<SystemRuleEntity>> {
    private final ConfigService configService;

    private final ObjectMapper objectMapper;

    public NacosSystemRuleApiProvider(ConfigService configService, ObjectMapper objectMapper) {
        this.configService = configService;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<SystemRuleEntity> getRules(String appName) throws Exception {
        String config =
            configService.getConfig(appName + NacosUtils.SYSTEM_DATA_ID_POSTFIX, NacosUtils.GROUP_ID, 5000L);
        if (config == null) {
            return null;
        }
        return this.objectMapper.readValue(config, new TypeReference<List<SystemRuleEntity>>() {});
    }
}
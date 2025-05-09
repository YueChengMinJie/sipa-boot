package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.alibaba.csp.sentinel.dashboard.common.SentinelNacosConstants;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AuthorityRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.nacos.api.config.ConfigService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author caszhou
 * @date 2024/12/17
 */
@Component
public class NacosAuthorityRuleApiProvider implements DynamicRuleProvider<List<AuthorityRuleEntity>> {
    private final ConfigService configService;

    private final ObjectMapper objectMapper;

    public NacosAuthorityRuleApiProvider(ConfigService configService, ObjectMapper objectMapper) {
        this.configService = configService;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<AuthorityRuleEntity> getRules(String appName) throws Exception {
        String config = configService.getConfig(appName + SentinelNacosConstants.AUTHORITY_FLOW_POSTFIX,
            SentinelNacosConstants.GROUP_ID, 5000L);
        if (config == null) {
            return Collections.emptyList();
        }
        List<AuthorityRule> authorityRuleEntities =
            this.objectMapper.readValue(config, new TypeReference<List<AuthorityRule>>() {});
        return authorityRuleEntities.stream()
            .map(authorityRule -> AuthorityRuleEntity.fromAuthorityRule(appName, "", 0, authorityRule))
            .collect(Collectors.toList());
    }
}

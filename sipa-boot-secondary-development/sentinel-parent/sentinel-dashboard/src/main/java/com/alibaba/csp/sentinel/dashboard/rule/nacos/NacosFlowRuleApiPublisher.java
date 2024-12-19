package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.dashboard.util.NacosUtils;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author caszhou
 * @date 2024/12/17
 */
@Component
public class NacosFlowRuleApiPublisher implements DynamicRulePublisher<List<FlowRuleEntity>> {
    private final ConfigService configService;

    private final ObjectMapper objectMapper;

    public NacosFlowRuleApiPublisher(ConfigService configService, ObjectMapper objectMapper) {
        this.configService = configService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(String app, List<FlowRuleEntity> rules) throws Exception {
        configService.publishConfig(app + NacosUtils.FLOW_DATA_ID_POSTFIX, NacosUtils.GROUP_ID,
            this.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rules), ConfigType.JSON.getType());
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
    }
}

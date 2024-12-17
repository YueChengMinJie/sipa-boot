package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import java.util.List;

import org.springframework.stereotype.Component;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;

/**
 * @author caszhou
 * @date 2024/12/17
 */
@Component
public class NacosFlowRuleApiPublisher implements DynamicRulePublisher<List<FlowRuleEntity>> {
    @Override
    public void publish(String app, List<FlowRuleEntity> rules) throws Exception {
        // todo
    }
}

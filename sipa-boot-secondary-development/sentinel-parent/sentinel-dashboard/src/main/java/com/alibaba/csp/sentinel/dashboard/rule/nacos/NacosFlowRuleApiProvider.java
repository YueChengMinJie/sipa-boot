package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import java.util.List;

import org.springframework.stereotype.Component;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;

/**
 * @author caszhou
 * @date 2024/12/17
 */
@Component
public class NacosFlowRuleApiProvider implements DynamicRuleProvider<List<FlowRuleEntity>> {
    @Override
    public List<FlowRuleEntity> getRules(String appName) throws Exception {
        return null;
    }
}

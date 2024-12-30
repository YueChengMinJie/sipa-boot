package com.sipa.boot.sentinel.server;

import java.util.List;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.env.Environment;

import com.alibaba.cloud.sentinel.SentinelProperties;
import com.alibaba.cloud.sentinel.custom.SentinelDataSourceHandler;
import com.alibaba.cloud.sentinel.datasource.config.AbstractDataSourceProperties;
import com.alibaba.cloud.sentinel.datasource.config.NacosDataSourceProperties;
import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterFlowRuleManager;
import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterParamFlowRuleManager;
import com.alibaba.csp.sentinel.datasource.AbstractDataSource;

import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2024/12/21
 */
@Slf4j
public class SipaSentinelDataSourceHandler extends SentinelDataSourceHandler {
    private final DefaultListableBeanFactory beanFactory;

    private final SentinelProperties sentinelProperties;

    private final Environment env;

    public SipaSentinelDataSourceHandler(DefaultListableBeanFactory beanFactory, SentinelProperties sentinelProperties,
        Environment env) {
        super(beanFactory, sentinelProperties, env);
        this.beanFactory = beanFactory;
        this.sentinelProperties = sentinelProperties;
        this.env = env;
    }

    @Override
    public void afterSingletonsInstantiated() {
        sentinelProperties.getDatasource().forEach((dataSourceName, dataSourceProperties) -> {
            try {
                List<String> validFields = dataSourceProperties.getValidField();
                if (validFields.size() != 1) {
                    log.error("[Sentinel Starter] DataSource " + dataSourceName
                        + " multi datasource active and won't loaded: " + dataSourceProperties.getValidField());
                    return;
                }
                AbstractDataSourceProperties abstractDataSourceProperties =
                    dataSourceProperties.getValidDataSourceProperties();
                abstractDataSourceProperties.setEnv(env);
                abstractDataSourceProperties.preCheck(dataSourceName);
                registerBeanWithCluster(abstractDataSourceProperties,
                    dataSourceName + "-sentinel-" + validFields.get(0) + "-datasource");
            } catch (Exception e) {
                log.error("[Sentinel Starter] DataSource " + dataSourceName + " build error: " + e.getMessage(), e);
            }
        });
    }

    private void registerBeanWithCluster(final AbstractDataSourceProperties dataSourceProperties,
        String dataSourceName) {
        BeanDefinitionBuilder builder = parseBeanDefinition(dataSourceProperties, dataSourceName);

        this.beanFactory.registerBeanDefinition(dataSourceName, builder.getBeanDefinition());
        // init in Spring
        AbstractDataSource newDataSource = (AbstractDataSource)this.beanFactory.getBean(dataSourceName);

        // register property in RuleManager
        dataSourceProperties.postRegister(newDataSource);

        if (dataSourceProperties instanceof NacosDataSourceProperties) {
            NacosDataSourceProperties nacosDataSourceProperties = (NacosDataSourceProperties)dataSourceProperties;
            if (nacosDataSourceProperties.getDataId().endsWith(NacosUtils.PARAM_FLOW_DATA_ID_POSTFIX)
                || nacosDataSourceProperties.getDataId().endsWith(NacosUtils.PARAM_FLOW_DATA_ID_POSTFIX)) {
                ClusterParamFlowRuleManager.setPropertySupplier(namespace -> newDataSource.getProperty());
            } else if (nacosDataSourceProperties.getDataId().endsWith(NacosUtils.FLOW_DATA_ID_POSTFIX)
                || nacosDataSourceProperties.getDataId().endsWith(NacosUtils.PARAM_FLOW_DATA_ID_POSTFIX)) {
                ClusterFlowRuleManager.setPropertySupplier(namespace -> newDataSource.getProperty());
            }
        }
    }
}

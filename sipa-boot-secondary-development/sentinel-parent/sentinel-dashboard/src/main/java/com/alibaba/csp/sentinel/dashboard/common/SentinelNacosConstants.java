package com.alibaba.csp.sentinel.dashboard.common;

/**
 * @author caszhou
 * @date 2024/12/19
 */
public interface SentinelNacosConstants {
    String GROUP_ID = "SENTINEL_GROUP";

    String FLOW_DATA_ID_POSTFIX = "-flow-rules";

    String DEGRADE_DATA_ID_POSTFIX = "-degrade-rules";

    String PARAM_FLOW_POSTFIX = "-param-flow-rules";

    String SYSTEM_DATA_ID_POSTFIX = "-system-rules";

    String AUTHORITY_FLOW_POSTFIX = "-authority-rules";

    String GW_FLOW_POSTFIX = "-gw-flow-rules";

    String GW_API_GROUP_POSTFIX = "-gw-api-group-rules";
}

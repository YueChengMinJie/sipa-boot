package com.sipa.boot.iot.protocol.mqtt;

import cn.hutool.core.date.DatePattern;

/**
 * @author caszhou
 * @date 2022/7/16
 */
public interface MqttProtocol {
    String DATA_FORMAT = DatePattern.NORM_DATETIME_PATTERN;

    MqttHeader getHeader();

    Object getDataBody();

    default String getDataSign() {
        // todo by caszhou 暂时返回null
        return null;
    }
}

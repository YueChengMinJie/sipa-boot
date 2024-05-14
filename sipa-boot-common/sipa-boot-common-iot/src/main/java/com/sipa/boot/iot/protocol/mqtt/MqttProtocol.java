package com.sipa.boot.iot.protocol.mqtt;

/**
 * @author caszhou
 * @date 2022/7/16
 */
public interface MqttProtocol {

    public static String DATA_FORMAT = "yyyy-MM-dd hh:mm:ss";
    MqttHeader getHeader();

    Object getDataBody();

    default String getDataSign() {
        // todo by caszhou 暂时返回null
        return null;
    }
}

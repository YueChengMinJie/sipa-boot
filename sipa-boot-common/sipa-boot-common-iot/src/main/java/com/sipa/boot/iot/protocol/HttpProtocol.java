package com.sipa.boot.iot.protocol;

import java.util.Map;

/**
 * @author caszhou
 * @date 2023/11/16
 */
public interface HttpProtocol<T> {
    Map<String, String> headers();

    T payload();

    int crc();
}

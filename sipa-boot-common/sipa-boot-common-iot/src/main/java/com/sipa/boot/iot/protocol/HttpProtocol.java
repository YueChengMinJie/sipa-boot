package com.sipa.boot.iot.protocol;

import java.util.Map;

/**
 * @author caszhou
 * @date 2023/11/16
 */
public interface HttpProtocol {
    Map<String, String> headers();

    Object payload();

    int crc();
}

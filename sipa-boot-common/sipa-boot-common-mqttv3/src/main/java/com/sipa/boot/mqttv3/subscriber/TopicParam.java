package com.sipa.boot.mqttv3.subscriber;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author caszhou
 * @date 2022/6/23
 */
@Setter
@Getter
@AllArgsConstructor
class TopicParam {
    private String name;

    // 正则匹配的参数位置.
    private int at;
}

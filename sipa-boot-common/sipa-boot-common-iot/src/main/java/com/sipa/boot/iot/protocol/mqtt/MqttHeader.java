package com.sipa.boot.iot.protocol.mqtt;

import lombok.*;

/**
 * @author caszhou
 * @date 2022/7/16
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MqttHeader {
    private String version;

    private String timeStamp;

    private Integer index;

    private String function;

    /**
     * 1-变化上送；2-周期发送；3-召唤上送
     */
    private Integer reason;
}

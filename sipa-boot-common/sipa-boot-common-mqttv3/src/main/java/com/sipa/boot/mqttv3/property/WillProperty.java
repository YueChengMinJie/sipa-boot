package com.sipa.boot.mqttv3.property;

import lombok.Getter;
import lombok.Setter;

/**
 * 遗愿相关配置
 * 
 * @author caszhou
 * @date 2022/6/23
 */
@Setter
@Getter
public class WillProperty {
    /**
     * 遗愿主题. -- GETTER -- 遗愿主题.
     */
    private String topic;

    /**
     * 遗愿消息内容. -- GETTER -- 遗愿消息内容.
     */
    private String payload;

    /**
     * 遗愿消息QOS. -- GETTER -- 遗愿消息QOS.
     */
    private Integer qos;

    /**
     * 遗愿消息是否保留. -- GETTER -- 遗愿消息是否保留.
     */
    private Boolean retained;
}

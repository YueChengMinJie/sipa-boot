package com.sipa.boot.test.mqtt;

import org.springframework.stereotype.Component;

import com.sipa.boot.mqttv3.annotation.MqttSubscribe;
import com.sipa.boot.mqttv3.annotation.NamedValue;
import com.sipa.boot.mqttv3.annotation.Payload;
import com.sipa.boot.test.controller.TestController;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2024/1/11
 */
@Slf4j
@Component
public class TestMqttSubscribe {
    @MqttSubscribe(value = "1000201/{stationCode}/S2M/event")
    public void test(@NamedValue(value = "stationCode") String stationCode,
        @Payload TestController.GlueAlert glueAlert) {
        log.info(stationCode);
        log.info(JSONUtil.toJsonStr(glueAlert));
    }
}

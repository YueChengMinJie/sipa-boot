package com.sipa.boot.test.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import com.sipa.boot.core.tool.uid.UidUtil;
import com.sipa.boot.iot.protocol.mqtt.MqttHeader;
import com.sipa.boot.iot.protocol.mqtt.MqttProtocol;
import com.sipa.boot.mqttv3.publisher.MqttPublisher;
import com.sipa.boot.test.form.TestForm;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2023/8/10
 */
@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class TestController {
    // private final SourceContext source;
    private final ApplicationEventPublisher publisher;

    private final MqttPublisher mqttPublisher;

    @GetMapping("/test/get")
    public void test(LocalDateTime time) {
        log.info(String.valueOf(time));
    }

    @PostMapping("/test/post")
    public void test(@RequestBody TestForm form) {
        log.info(String.valueOf(form.getTime()));
    }

    // @GetMapping("/test/rmq/producer")
    // public void testRmqProducer() {
    // Map<String, Object> headers = new HashMap<>();
    // headers.put(MessageConst.PROPERTY_TAGS, "tagStr");
    // RocketMQScs payload = new RocketMQScs();
    // payload.setTestEnum(TestEnum.TRADE_TYPE_OF_RECHARGE);
    // Message<RocketMQScs> message = MessageBuilder.createMessage(payload, new MessageHeaders(headers));
    // // source.output().send(message);
    // }
    //
    // @GetMapping("/test/rmq/bus")
    // public void testRmpBus() {
    // publisher.publishEvent(
    // new RocketMQScsRemoteApplicationEvent(this, new RocketMQScs(), SpringUtil.getApplicationName()));
    // }

    @SneakyThrows
    @GetMapping("/test/mqtt/producer")
    public void testMqttProducer() {
        mqttPublisher.send("1000201/28-1-2/S2M/event", GlueAlert.of(1, Map.of("sn", UidUtil.nextSid())));
    }

    @Data
    public static class GlueAlert implements MqttProtocol {
        private MqttHeader header;

        private Object dataBody;

        public static GlueAlert of(Long index, Object dataBody) {
            GlueAlert glueAlert = new GlueAlert();
            glueAlert.setHeader(getHeader(index));
            glueAlert.setDataBody(dataBody);
            return glueAlert;
        }

        private static MqttHeader getHeader(Long index) {
            return MqttHeader.builder()
                .version("1.0.0")
                .timeStamp(String.valueOf(System.currentTimeMillis()))
                .index(index)
                .function("glueAlert")
                .reason(1)
                .build();
        }
    }
}

package com.sipa.boot.test.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.sipa.boot.test.rmq.bean.RocketMQScs;
import com.sipa.boot.test.rmq.bean.TestEnum;
import com.sipa.boot.test.rmq.context.SourceContext;
import com.sipa.boot.test.rmq.event.RocketMQScsRemoteApplicationEvent;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import com.sipa.boot.test.form.TestForm;

import cn.hutool.extra.spring.SpringUtil;
import lombok.RequiredArgsConstructor;
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
    private final SourceContext source;

    private final ApplicationEventPublisher publisher;

    @GetMapping("/test/get")
    public void test(LocalDateTime time) {
        log.info(String.valueOf(time));
    }

    @PostMapping("/test/post")
    public void test(@RequestBody TestForm form) {
        log.info(String.valueOf(form.getTime()));
    }

    @GetMapping("/test/rmq/producer")
    public void testRmqProducer() {
        Map<String, Object> headers = new HashMap<>();
        headers.put(MessageConst.PROPERTY_TAGS, "tagStr");
        RocketMQScs payload = new RocketMQScs();
        payload.setTestEnum(TestEnum.TRADE_TYPE_OF_RECHARGE);
        Message<RocketMQScs> message = MessageBuilder.createMessage(payload, new MessageHeaders(headers));
        source.output().send(message);
    }

    @GetMapping("/test/rmq/bus")
    public void testRmpBus() {
        publisher.publishEvent(
            new RocketMQScsRemoteApplicationEvent(this, new RocketMQScs(), SpringUtil.getApplicationName()));
    }
}

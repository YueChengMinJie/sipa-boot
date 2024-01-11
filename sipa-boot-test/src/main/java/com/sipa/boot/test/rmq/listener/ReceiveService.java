// package com.sipa.boot.test.rmq.listener;
//
// import com.sipa.boot.test.rmq.bean.RocketMQScs;
// import com.sipa.boot.test.rmq.bean.TestDto;
// import com.sipa.boot.test.rmq.event.RocketMQScsRemoteApplicationEvent;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
// import org.springframework.cloud.stream.annotation.StreamListener;
// import org.springframework.context.event.EventListener;
// import org.springframework.stereotype.Service;
//
// import com.sipa.boot.test.rmq.context.SinkContext;
//
// import cn.hutool.extra.spring.SpringUtil;
// import cn.hutool.json.JSONUtil;
// import lombok.extern.slf4j.Slf4j;
//
/// **
// * @author caszhou
// * @date 2023/8/11
// */
// @Slf4j
// @Service
// public class ReceiveService {
// @StreamListener(SinkContext.INPUT1)
// public void receiveInput1(RocketMQScs rocketMQScs) {
// log.info("input1 receive: " + JSONUtil.toJsonPrettyStr(rocketMQScs));
// }
//
// @StreamListener(SinkContext.INPUT2)
// public void receiveInput2(RocketMQScs rocketMQScs) {
// log.info("input2 receive: " + rocketMQScs);
// }
//
// @StreamListener(SinkContext.INPUT3)
// public void receiveInput2(TestDto dto) {
// log.info("input3 receive: " + JSONUtil.toJsonPrettyStr(dto));
// }
//
// @EventListener
// @ConditionalOnExpression("${eventbus} == 1")
// public void onEvent(RocketMQScsRemoteApplicationEvent event) {
// log.info("Server [port : {}] listeners on {}", SpringUtil.getProperty("server.port"), event.getRocketMQScs());
// }
//
// @EventListener
// @ConditionalOnExpression("${eventbus} == 1")
// public void onAckEvent(RocketMQScsRemoteApplicationEvent event) {
// log.info("Server [port : {}] listeners on {}", SpringUtil.getProperty("server.port"),
// JSONUtil.toJsonPrettyStr(event));
// }
// }

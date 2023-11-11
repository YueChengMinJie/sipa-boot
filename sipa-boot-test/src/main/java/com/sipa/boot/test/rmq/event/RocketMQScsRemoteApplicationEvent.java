package com.sipa.boot.test.rmq.event;

import org.springframework.cloud.bus.event.RemoteApplicationEvent;

import com.sipa.boot.test.rmq.bean.RocketMQScs;

import lombok.Data;

@Data
public class RocketMQScsRemoteApplicationEvent extends RemoteApplicationEvent {
    private static final long serialVersionUID = 5619701810881787244L;

    private RocketMQScs rocketMQScs;

    public RocketMQScsRemoteApplicationEvent(Object source, RocketMQScs rocketMQScs, String originService) {
        super(source, originService, DEFAULT_DESTINATION_FACTORY.getDestination(originService));
        this.rocketMQScs = rocketMQScs;
    }
}

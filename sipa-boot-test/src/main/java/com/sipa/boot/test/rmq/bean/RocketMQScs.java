package com.sipa.boot.test.rmq.bean;

import lombok.Data;

/**
 * @author caszhou
 * @date 2023/8/11
 */
@Data
public class RocketMQScs {
    private String hello = "world";

    private TestEnum testEnum;
}

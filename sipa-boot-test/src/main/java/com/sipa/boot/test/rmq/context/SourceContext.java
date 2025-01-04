package com.sipa.boot.test.rmq.context;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * @author caszhou
 * @date 2023/8/11
 */
public interface SourceContext {
    String OUTPUT = "output";

    @Output(OUTPUT)
    MessageChannel output();
}

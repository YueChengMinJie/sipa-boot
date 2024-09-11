package com.sipa.boot.ws;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Configuration
@EnableWebSocket
@ComponentScan("com.sipa.boot.ws.**")
@ConditionalOnClass(SipaServerEndpointConfigurator.class)
public class WsAutoConfiguration {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}

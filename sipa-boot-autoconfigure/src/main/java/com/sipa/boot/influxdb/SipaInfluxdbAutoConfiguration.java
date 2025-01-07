package com.sipa.boot.influxdb;

import java.util.Collections;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.influxdb.client.flux.FluxClient;
import com.influxdb.client.flux.FluxClientFactory;
import com.influxdb.client.flux.FluxConnectionOptions;
import com.influxdb.spring.health.InfluxDB2HealthIndicatorAutoConfiguration;
import com.influxdb.spring.influx.InfluxDB2AutoConfiguration;
import com.influxdb.spring.influx.InfluxDB2Properties;
import com.sipa.boot.influxdb.env.InfluxdbEnvProcessor;

import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

/**
 * @author caszhou
 * @date 2025/1/4
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnClass(InfluxdbEnvProcessor.class)
@Import(value = {InfluxDB2AutoConfiguration.class, InfluxDB2HealthIndicatorAutoConfiguration.class})
public class SipaInfluxdbAutoConfiguration {
    private final InfluxDB2Properties properties;

    @Bean
    @ConditionalOnProperty("influx.url")
    @ConditionalOnMissingBean(FluxClient.class)
    public FluxClient fluxClient() {
        OkHttpClient.Builder okHttpBuilder =
            new OkHttpClient.Builder().protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .readTimeout(properties.getReadTimeout())
                .writeTimeout(properties.getWriteTimeout())
                .connectTimeout(properties.getConnectTimeout());

        return FluxClientFactory
            .create(FluxConnectionOptions.builder().url(properties.getUrl()).okHttpClient(okHttpBuilder).build());
    }
}

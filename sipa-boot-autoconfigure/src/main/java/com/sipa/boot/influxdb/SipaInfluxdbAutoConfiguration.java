package com.sipa.boot.influxdb;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.influxdb.spring.health.InfluxDB2HealthIndicatorAutoConfiguration;
import com.influxdb.spring.influx.InfluxDB2AutoConfiguration;

/**
 * @author caszhou
 * @date 2025/1/4
 */
@Configuration
@Import(value = {InfluxDB2AutoConfiguration.class, InfluxDB2HealthIndicatorAutoConfiguration.class})
public class SipaInfluxdbAutoConfiguration {
    //
}

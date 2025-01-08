package com.sipa.boot.influxdb.env;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.influxdb.spring.influx.InfluxDB2Properties;

import lombok.Getter;

/**
 * @author caszhou
 * @date 2025/1/8
 */
@Component
public class InfluxdbUtil {
    @Getter
    private static InfluxDB2Properties properties;

    public InfluxdbUtil(InfluxDB2Properties properties) {
        InfluxdbUtil.properties = properties;
    }

    public String getBucket() {
        return Optional.ofNullable(properties).orElse(new InfluxDB2Properties()).getBucket();
    }

    public String getOrg() {
        return Optional.ofNullable(properties).orElse(new InfluxDB2Properties()).getOrg();
    }

    public Map<String, Object> getParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("bucketParam", getBucket());
        return params;
    }
}

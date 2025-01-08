package com.alibaba.csp.sentinel.dashboard.datasource.timeseries;

import java.time.Instant;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

/**
 * @author caszhou
 * @date 2025/1/8
 */
@Measurement(name = "rt")
public class RtMetric {
    @Column(tag = true)
    private String app;

    @Column(tag = true)
    private String resource;

    @Column
    private double value;

    @Column(timestamp = true)
    private Instant time;

    RtMetric(String app, String resource, double value, Instant time) {
        this.app = app;
        this.resource = resource;
        this.value = value;
        this.time = time;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }
}

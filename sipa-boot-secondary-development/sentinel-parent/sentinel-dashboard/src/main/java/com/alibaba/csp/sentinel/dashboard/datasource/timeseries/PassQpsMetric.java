package com.alibaba.csp.sentinel.dashboard.datasource.timeseries;

import java.time.Instant;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

/**
 * @author caszhou
 * @date 2025/1/8
 */
@Measurement(name = "passQps")
public class PassQpsMetric {
    @Column(tag = true)
    private String app;

    @Column(tag = true)
    private String resource;

    @Column
    private long value;

    @Column(timestamp = true)
    private Instant time;

    PassQpsMetric(String app, String resource, long value, Instant time) {
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

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }
}

package com.alibaba.csp.sentinel.dashboard.datasource.timeseries;

import java.time.Instant;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import com.influxdb.query.FluxRecord;

/**
 * @author caszhou
 * @date 2025/1/8
 */
public class AllMetric {
    private PassQpsMetric passQps;

    private SuccessQpsMetric successQpsMetric;

    private BlockQpsMetric blockQpsMetric;

    private ExceptionQpsMetric exceptionQpsMetric;

    private RtMetric rtMetric;

    private CountMetric countMetric;

    private AllMetric() {
        //
    }

    public PassQpsMetric getPassQps() {
        return passQps;
    }

    public void setPassQps(PassQpsMetric passQps) {
        this.passQps = passQps;
    }

    public SuccessQpsMetric getSuccessQpsMetric() {
        return successQpsMetric;
    }

    public void setSuccessQpsMetric(SuccessQpsMetric successQpsMetric) {
        this.successQpsMetric = successQpsMetric;
    }

    public BlockQpsMetric getBlockQpsMetric() {
        return blockQpsMetric;
    }

    public void setBlockQpsMetric(BlockQpsMetric blockQpsMetric) {
        this.blockQpsMetric = blockQpsMetric;
    }

    public ExceptionQpsMetric getExceptionQpsMetric() {
        return exceptionQpsMetric;
    }

    public void setExceptionQpsMetric(ExceptionQpsMetric exceptionQpsMetric) {
        this.exceptionQpsMetric = exceptionQpsMetric;
    }

    public RtMetric getRtMetric() {
        return rtMetric;
    }

    public void setRtMetric(RtMetric rtMetric) {
        this.rtMetric = rtMetric;
    }

    public CountMetric getCountMetric() {
        return countMetric;
    }

    public void setCountMetric(CountMetric countMetric) {
        this.countMetric = countMetric;
    }

    public static AllMetric of(MetricEntity metricEntity) {
        if (metricEntity == null) {
            return null;
        }

        String app = metricEntity.getApp();
        String resource = metricEntity.getResource();
        Instant instant = metricEntity.getTimestamp().toInstant();

        AllMetric allMetric = new AllMetric();
        allMetric.setPassQps(new PassQpsMetric(app, resource,
            metricEntity.getPassQps() == null ? 0L : metricEntity.getPassQps(), instant));
        allMetric.setSuccessQpsMetric(new SuccessQpsMetric(app, resource,
            metricEntity.getSuccessQps() == null ? 0L : metricEntity.getSuccessQps(), instant));
        allMetric.setBlockQpsMetric(new BlockQpsMetric(app, resource,
            metricEntity.getBlockQps() == null ? 0L : metricEntity.getBlockQps(), instant));
        allMetric.setExceptionQpsMetric(new ExceptionQpsMetric(app, resource,
            metricEntity.getExceptionQps() == null ? 0L : metricEntity.getExceptionQps(), instant));
        allMetric.setRtMetric(new RtMetric(app, resource, metricEntity.getRt(), instant));
        allMetric.setCountMetric(new CountMetric(app, resource, metricEntity.getCount(), instant));
        return allMetric;
    }

    public static MetricEntity to(FluxRecord record) {
        MetricEntity metricEntity = new MetricEntity();

        return metricEntity;
    }
}

package com.alibaba.csp.sentinel.dashboard.datasource.entity.factory;

import java.util.Date;

import org.jetbrains.annotations.Nullable;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import com.influxdb.query.FluxRecord;

/**
 * @author caszhou
 * @date 2025/1/8
 */
public class MetricEntityFactory {
    public static MetricEntity make(String app, String resource, Date ts) {
        MetricEntity metricEntity = new MetricEntity();
        metricEntity.setId(ts.toInstant().toEpochMilli());
        metricEntity.setApp(app);
        metricEntity.setResource(resource);
        metricEntity.setTimestamp(ts);
        metricEntity.setGmtCreate(ts);
        metricEntity.setGmtModified(ts);
        return metricEntity;
    }

    private static int intGet(Object value) {
        return value == null ? 0 : Integer.parseInt(value.toString());
    }

    private static double doubleGet(Object value) {
        return value == null ? 0D : Double.parseDouble(value.toString());
    }

    private static @Nullable Long getLong(Object value) {
        return value == null ? null : Long.valueOf(value.toString());
    }

    public static void fill(FluxRecord fluxRecord, MetricEntity metricEntity) {
        String measurement = fluxRecord.getMeasurement();
        Object value = fluxRecord.getValue();
        if ("passQps".equals(measurement)) {
            metricEntity.setPassQps(getLong(value));
        } else if ("successQps".equals(measurement)) {
            metricEntity.setSuccessQps(getLong(value));
        } else if ("blockQps".equals(measurement)) {
            metricEntity.setBlockQps(getLong(value));
        } else if ("exceptionQps".equals(measurement)) {
            metricEntity.setExceptionQps(getLong(value));
        } else if ("rt".equals(measurement)) {
            metricEntity.setRt(doubleGet(value));
        } else if ("count".equals(measurement)) {
            metricEntity.setCount(intGet(value));
        }
    }
}

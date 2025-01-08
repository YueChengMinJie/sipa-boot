package com.alibaba.csp.sentinel.dashboard.repository.metric;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.factory.MetricEntityFactory;
import com.alibaba.csp.sentinel.dashboard.datasource.timeseries.AllMetric;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxColumn;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.influxdb.spring.influx.InfluxDB2Properties;

/**
 * @author caszhou
 * @date 2025/1/7
 */
@Component
public class InfluxdbMetricsRepository implements MetricsRepository<MetricEntity> {
    private final InfluxDBClient influxDBClient;

    private final InfluxDB2Properties properties;

    public InfluxdbMetricsRepository(InfluxDBClient influxDBClient, InfluxDB2Properties properties) {
        this.influxDBClient = influxDBClient;
        this.properties = properties;
    }

    @Override
    public void save(MetricEntity metric) {
        AllMetric allMetric = AllMetric.of(metric);
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writeMeasurement(WritePrecision.S, allMetric.getPassQps());
        writeApi.writeMeasurement(WritePrecision.S, allMetric.getSuccessQpsMetric());
        writeApi.writeMeasurement(WritePrecision.S, allMetric.getBlockQpsMetric());
        writeApi.writeMeasurement(WritePrecision.S, allMetric.getExceptionQpsMetric());
        writeApi.writeMeasurement(WritePrecision.S, allMetric.getRtMetric());
        writeApi.writeMeasurement(WritePrecision.S, allMetric.getCountMetric());
    }

    @Override
    public void saveAll(Iterable<MetricEntity> metrics) {
        if (metrics == null) {
            return;
        }
        metrics.forEach(this::save);
    }

    @Override
    public List<MetricEntity> queryByAppAndResourceBetween(String app, String resource, long startTime, long endTime) {
        QueryApi queryApi = influxDBClient.getQueryApi();
        String flux = "from(bucket: \"" + properties.getBucket() + "\") |> range(start: " + startTime / 1000
            + ", stop: " + ((endTime / 1000) + 1) + ")"

            + " |> filter(fn: (r) => r[\"_measurement\"] == \"passQps\" or r[\"_measurement\"] == \"successQps\" or r[\"_measurement\"] == \"blockQps\" or r[\"_measurement\"] == \"exceptionQps\" or r[\"_measurement\"] == \"rt\" or r[\"_measurement\"] == \"count\")"

            + " |> filter(fn: (r) => r[\"app\"] == \"" + app + "\" and r[\"resource\"] == \"" + resource + "\")"

            + " |> sort(columns: [\"_time\"], desc: true) |> limit(n:6)";
        List<FluxTable> metrics = queryApi.query(flux);
        Map<Date, MetricEntity> metricMap = new LinkedHashMap<>();
        for (FluxTable fluxTable : metrics) {
            for (FluxRecord record : fluxTable.getRecords()) {
                Date date = new Date(Objects.requireNonNull(record.getTime()).toEpochMilli());
                MetricEntity metricEntity =
                    metricMap.computeIfAbsent(date, d -> MetricEntityFactory.make(app, resource, d));
                MetricEntityFactory.fill(record, metricEntity);
            }
        }
        return new ArrayList<>(metricMap.values());
    }

    @Override
    public List<String> listResourcesOfApp(String app) {
        QueryApi queryApi = influxDBClient.getQueryApi();
        String flux = "from(bucket: \"" + properties.getBucket() + "\") |> range(start: -5m)"
            + " |> filter(fn: (r) => r[\"_measurement\"] == \"passQps\" and r[\"app\"] == \"" + app + "\")"
            + " |> group(columns: [\"resource\"]) |> keep(columns: [\"resource\"]) |> limit(n:1)";
        List<FluxTable> metrics = queryApi.query(flux);
        return metrics.stream().map(fluxTable -> {
            List<FluxColumn> fluxColumnList =
                fluxTable.getColumns().stream().filter(FluxColumn::isGroup).collect(Collectors.toList());
            FluxColumn fluxColumn = fluxColumnList.get(0);
            int index = fluxColumn.getIndex();
            Object value = fluxTable.getRecords().get(0).getValueByIndex(index);
            return value == null ? null : String.valueOf(value);
        }).collect(Collectors.toList());
    }
}

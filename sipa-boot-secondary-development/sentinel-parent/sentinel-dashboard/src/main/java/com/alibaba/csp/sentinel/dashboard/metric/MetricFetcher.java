/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.alibaba.csp.sentinel.dashboard.metric;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.csp.sentinel.Constants;
import com.alibaba.csp.sentinel.concurrent.NamedThreadFactory;
import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import com.alibaba.csp.sentinel.dashboard.discovery.AppInfo;
import com.alibaba.csp.sentinel.dashboard.discovery.AppManagement;
import com.alibaba.csp.sentinel.dashboard.discovery.MachineInfo;
import com.alibaba.csp.sentinel.dashboard.repository.metric.MetricsRepository;
import com.alibaba.csp.sentinel.node.metric.MetricNode;
import com.alibaba.csp.sentinel.util.StringUtil;

/**
 * Fetch metric of machines.
 *
 * @author leyou
 */
@Component
public class MetricFetcher {
    public static final String NO_METRICS = "No metrics";

    private static final int HTTP_OK = 200;

    private static final long MAX_LAST_FETCH_INTERVAL_MS = 1000 * 15;

    private static final long FETCH_INTERVAL_SECOND = 6;

    private static final Charset DEFAULT_CHARSET = Charset.forName(SentinelConfig.charset());

    private static final String METRIC_URL_PATH = "metric";

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricFetcher.class);

    private static final Set<String> RES_EXCLUSION_SET = new HashSet<String>() {
        private static final long serialVersionUID = 6121475772766962368L;

        {
            add(Constants.TOTAL_IN_RESOURCE_NAME);
            add(Constants.SYSTEM_LOAD_RESOURCE_NAME);
            add(Constants.CPU_USAGE_RESOURCE_NAME);
        }
    };

    private final long intervalSecond;

    @SuppressWarnings("PMD.ThreadPoolCreationRule")
    private final ScheduledExecutorService fetchScheduleService;

    private final Map<String, AtomicLong> appLastFetchTime;

    private final ExecutorService fetchService;

    private final ExecutorService fetchWorker;

    private final CloseableHttpAsyncClient httpclient;

    @Autowired
    private MetricsRepository<MetricEntity> metricStore;

    @Autowired
    private AppManagement appManagement;

    public MetricFetcher() {
        intervalSecond = 1;

        fetchScheduleService =
            Executors.newScheduledThreadPool(1, new NamedThreadFactory("sentinel-dashboard-metrics-fetch-task", true));

        appLastFetchTime = new ConcurrentHashMap<>();

        int cores = Runtime.getRuntime().availableProcessors() * 2;
        long keepAliveTime = 0;
        int queueSize = 2048;
        RejectedExecutionHandler handler = new DiscardPolicy();
        fetchService = new ThreadPoolExecutor(cores, cores, keepAliveTime, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(queueSize),
            new NamedThreadFactory("sentinel-dashboard-metrics-fetchService", true), handler);
        fetchWorker = new ThreadPoolExecutor(cores, cores, keepAliveTime, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(queueSize), new NamedThreadFactory("sentinel-dashboard-metrics-fetchWorker", true),
            handler);

        IOReactorConfig ioConfig = IOReactorConfig.custom()
            .setConnectTimeout(3000)
            .setSoTimeout(3000)
            .setIoThreadCount(Runtime.getRuntime().availableProcessors() * 2)
            .build();
        httpclient = HttpAsyncClients.custom().setRedirectStrategy(new DefaultRedirectStrategy() {
            @Override
            protected boolean isRedirectable(final String method) {
                return false;
            }
        }).setMaxConnTotal(4000).setMaxConnPerRoute(1000).setDefaultIOReactorConfig(ioConfig).build();
        httpclient.start();

        start();
    }

    private void start() {
        fetchScheduleService.scheduleAtFixedRate(() -> {
            try {
                fetchAllApp();
            } catch (Exception e) {
                LOGGER.info("fetchAllApp error:", e);
            }
        }, 10, intervalSecond, TimeUnit.SECONDS);
    }

    /**
     * Traverse each APP, and then pull the metric of all machines for that APP.
     */
    private void fetchAllApp() {
        List<String> apps = appManagement.getAppNames();
        if (apps == null) {
            return;
        }
        for (final String app : apps) {
            fetchService.submit(() -> {
                try {
                    doFetchAppMetric(app);
                } catch (Exception e) {
                    LOGGER.error("fetchAppMetric error", e);
                }
            });
        }
    }

    private void doFetchAppMetric(final String app) {
        long now = System.currentTimeMillis();
        long lastFetchMs = now - MAX_LAST_FETCH_INTERVAL_MS;
        if (appLastFetchTime.containsKey(app)) {
            lastFetchMs = Math.max(lastFetchMs, appLastFetchTime.get(app).get() + 1000);
        }
        // trim milliseconds
        lastFetchMs = lastFetchMs / 1000 * 1000;
        long endTime = lastFetchMs + FETCH_INTERVAL_SECOND * 1000;
        if (endTime > now - 1000 * 2) {
            // too near
            return;
        }
        // update last_fetch in advance.
        appLastFetchTime.computeIfAbsent(app, a -> new AtomicLong()).set(endTime);
        final long finalLastFetchMs = lastFetchMs;
        final long finalEndTime = endTime;
        try {
            // do real fetch async
            fetchWorker.submit(() -> {
                try {
                    fetchOnce(app, finalLastFetchMs, finalEndTime, 5);
                } catch (Exception e) {
                    LOGGER.info("fetchOnce(" + app + ") error", e);
                }
            });
        } catch (Exception e) {
            LOGGER.info("submit fetchOnce(" + app + ") fail, intervalMs [" + lastFetchMs + ", " + endTime + "]", e);
        }
    }

    /**
     * fetch metric between [startTime, endTime], both side inclusive
     */
    private void fetchOnce(String app, long startTime, long endTime, int maxWaitSeconds) {
        if (maxWaitSeconds <= 0) {
            throw new IllegalArgumentException("maxWaitSeconds must > 0, but " + maxWaitSeconds);
        }
        AppInfo appInfo = appManagement.getDetailApp(app);
        // auto remove for app
        if (appInfo.isDead()) {
            LOGGER.info("Dead app removed: {}", app);
            appManagement.removeApp(app);
            return;
        }
        Set<MachineInfo> machines = appInfo.getMachines();
        LOGGER.debug("enter fetchOnce(" + app + "), machines.size()=" + machines.size() + ", time intervalMs ["
            + startTime + ", " + endTime + "]");
        if (machines.isEmpty()) {
            return;
        }
        final String msg = "fetch";
        final AtomicLong unhealthy = new AtomicLong();
        final AtomicLong success = new AtomicLong();
        final AtomicLong fail = new AtomicLong();

        // long start = System.currentTimeMillis();
        /** app_resource_timeSecond -> metric */
        final Map<String, MetricEntity> metricMap = new ConcurrentHashMap<>(16);
        final CountDownLatch latch = new CountDownLatch(machines.size());
        for (final MachineInfo machine : machines) {
            // auto remove
            if (machine.isDead()) {
                latch.countDown();
                appManagement.getDetailApp(app).removeMachine(machine.getIp(), machine.getPort());
                LOGGER.info("Dead machine removed: {}:{} of {}", machine.getIp(), machine.getPort(), app);
                continue;
            }
            if (!machine.isHealthy()) {
                latch.countDown();
                unhealthy.incrementAndGet();
                continue;
            }
            final String url = "http://" + machine.getIp() + ":" + machine.getPort() + "/" + METRIC_URL_PATH
                + "?startTime=" + startTime + "&endTime=" + endTime + "&refetch=" + false;
            final HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
            httpclient.execute(httpGet, new FutureCallback<HttpResponse>() {
                @Override
                public void completed(final HttpResponse response) {
                    try {
                        handleResponse(response, machine, metricMap);
                        success.incrementAndGet();
                    } catch (Exception e) {
                        LOGGER.error(msg + " metric " + url + " error:", e);
                    } finally {
                        latch.countDown();
                    }
                }

                @Override
                public void failed(final Exception ex) {
                    latch.countDown();
                    fail.incrementAndGet();
                    httpGet.abort();
                    if (ex instanceof SocketTimeoutException) {
                        LOGGER.error("Failed to fetch metric from <{}>: socket timeout", url);
                    } else if (ex instanceof ConnectException) {
                        LOGGER.error("Failed to fetch metric from <{}> (ConnectionException: {})", url,
                            ex.getMessage());
                    } else {
                        LOGGER.error(msg + " metric " + url + " error", ex);
                    }
                }

                @Override
                public void cancelled() {
                    latch.countDown();
                    fail.incrementAndGet();
                    httpGet.abort();
                }
            });
        }
        try {
            latch.await(maxWaitSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.info(msg + " metric, wait http client error:", e);
        }
        // long cost = System.currentTimeMillis() - start;
        // logger.info("finished " + msg + " metric for " + app + ", time intervalMs [" + startTime + ", " + endTime
        // + "], total machines=" + machines.size() + ", dead=" + dead + ", fetch success="
        // + success + ", fetch fail=" + fail + ", time cost=" + cost + " ms");
        writeMetric(metricMap);
    }

    private void handleResponse(final HttpResponse response, MachineInfo machine, Map<String, MetricEntity> metricMap)
        throws Exception {
        int code = response.getStatusLine().getStatusCode();
        if (code != HTTP_OK) {
            return;
        }
        Charset charset = null;
        try {
            String contentTypeStr = response.getFirstHeader("Content-type").getValue();
            if (StringUtil.isNotEmpty(contentTypeStr)) {
                ContentType contentType = ContentType.parse(contentTypeStr);
                charset = contentType.getCharset();
            }
        } catch (Exception ignore) {
        }
        String body = EntityUtils.toString(response.getEntity(), charset != null ? charset : DEFAULT_CHARSET);
        if (StringUtil.isEmpty(body) || body.startsWith(NO_METRICS)) {
            // logger.info(machine.getApp() + ":" + machine.getIp() + ":" + machine.getPort() + ", bodyStr is empty");
            return;
        }
        String[] lines = body.split("\n");
        // logger.info(machine.getApp() + ":" + machine.getIp() + ":" + machine.getPort() +
        // ", bodyStr.length()=" + body.length() + ", lines=" + lines.length);
        handleBody(lines, machine, metricMap);
    }

    private void handleBody(String[] lines, MachineInfo machine, Map<String, MetricEntity> map) {
        // logger.info("handleBody() lines=" + lines.length + ", machine=" + machine);
        if (lines.length < 1) {
            return;
        }

        for (String line : lines) {
            try {
                MetricNode node = MetricNode.fromThinString(line);
                if (shouldFilterOut(node.getResource())) {
                    continue;
                }
                /*
                 * aggregation metrics by app_resource_timeSecond, ignore ip and port.
                 */
                String key = buildMetricKey(machine.getApp(), node.getResource(), node.getTimestamp());

                MetricEntity metricEntity = map.computeIfAbsent(key, s -> {
                    MetricEntity initMetricEntity = new MetricEntity();
                    initMetricEntity.setApp(machine.getApp());
                    initMetricEntity.setTimestamp(new Date(node.getTimestamp()));
                    initMetricEntity.setPassQps(0L);
                    initMetricEntity.setBlockQps(0L);
                    initMetricEntity.setRtAndSuccessQps(0, 0L);
                    initMetricEntity.setExceptionQps(0L);
                    initMetricEntity.setCount(0);
                    initMetricEntity.setResource(node.getResource());
                    return initMetricEntity;
                });
                metricEntity.addPassQps(node.getPassQps());
                metricEntity.addBlockQps(node.getBlockQps());
                metricEntity.addRtAndSuccessQps(node.getRt(), node.getSuccessQps());
                metricEntity.addExceptionQps(node.getExceptionQps());
                metricEntity.addCount(1);
            } catch (Exception e) {
                LOGGER.warn("handleBody line exception, machine: {}, line: {}", machine.toLogString(), line);
            }
        }
    }

    private boolean shouldFilterOut(String resource) {
        return RES_EXCLUSION_SET.contains(resource);
    }

    private String buildMetricKey(String app, String resource, long timestamp) {
        return app + "__" + resource + "__" + (timestamp / 1000);
    }

    private void writeMetric(Map<String, MetricEntity> map) {
        if (map.isEmpty()) {
            return;
        }
        Date date = new Date();
        for (MetricEntity entity : map.values()) {
            entity.setGmtCreate(date);
            entity.setGmtModified(date);
        }
        metricStore.saveAll(map.values());
    }
}

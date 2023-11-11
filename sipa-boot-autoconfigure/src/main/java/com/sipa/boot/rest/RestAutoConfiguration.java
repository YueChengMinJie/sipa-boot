package com.sipa.boot.rest;

import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipa.boot.rest.handler.RestTemplateResponseErrorHandler;
import com.sipa.boot.rest.log.LogInterceptor;
import com.sipa.boot.rest.property.RestProperty;
import com.sipa.boot.rest.util.RestUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xiajiezhou
 * @date 2022/5/22
 */
@Slf4j
@Configuration
@ConditionalOnClass(RestUtil.class)
@ComponentScan("com.sipa.boot.rest.**")
@EnableConfigurationProperties(RestProperty.class)
@RequiredArgsConstructor
public class RestAutoConfiguration {
    private final RestProperty property;

    private static List<HttpMessageConverter<?>> getMessageConverters(ObjectMapper objectMapper) {
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();

        FormHttpMessageConverter formConverter = new FormHttpMessageConverter();

        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        jsonConverter.setSupportedMediaTypes(List.of(MediaType.ALL));

        return List.of(stringConverter, formConverter, jsonConverter);
    }

    @Bean
    public PoolingHttpClientConnectionManager poolingConnectionManager() {
        SSLContextBuilder builder = new SSLContextBuilder();
        try {
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            log.error("Pooling Connection Manager Initialisation failure because of {}", e.getMessage(), e);
        }
        SSLConnectionSocketFactory factory = null;
        try {
            factory = new SSLConnectionSocketFactory(builder.build());
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            log.error("Pooling Connection Manager Initialisation failure because of {}", e.getMessage(), e);
        }
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("https", Objects.requireNonNull(factory))
            .register("http", new PlainConnectionSocketFactory())
            .build();

        PoolingHttpClientConnectionManager poolingConnectionManager =
            new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        poolingConnectionManager.setMaxTotal(this.property.getMaxTotalConnections());
        return poolingConnectionManager;
    }

    @Bean
    public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
        return (response, context) -> {
            HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && this.property.getKeepAliveSettingKey().equalsIgnoreCase(param)) {
                    return Long.parseLong(value) * 1000;
                }
            }
            return this.property.getDefaultKeepAliveTimeMillis();
        };
    }

    @Bean
    public HttpRequestRetryHandler twiceRetryHandler() {
        return (exception, executionCount, context) -> {
            // 最多重试 3 次
            if (executionCount >= this.property.getIoExceptionRetryTime()) {
                return false;
            }
            // 连接超时异常 重试
            if (exception instanceof ConnectTimeoutException) {
                return true;
            }
            // 打断 不重试
            if (exception instanceof InterruptedIOException) {
                return false;
            }
            // DNS 解析异常不重试
            if (exception instanceof UnknownHostException) {
                return false;
            }
            // SSL 异常不重试
            if (exception instanceof SSLException) {
                return false;
            }
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            return !(request instanceof HttpEntityEnclosingRequest);
        };
    }

    @Bean
    public CloseableHttpClient httpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(this.property.getRequestTimeout())
            .setConnectTimeout(this.property.getConnectTimeout())
            .setSocketTimeout(this.property.getSocketTimeout())
            .build();
        return HttpClients.custom()
            .setDefaultRequestConfig(requestConfig)
            .setConnectionManager(this.poolingConnectionManager())
            .setConnectionManagerShared(true)
            .setKeepAliveStrategy(this.connectionKeepAliveStrategy())
            .setRetryHandler(this.twiceRetryHandler())
            .build();
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(this.httpClient());
        return clientHttpRequestFactory;
    }

    @Bean
    @LoadBalanced
    public RestTemplate lbRestTemplate(ObjectMapper objectMapper) {
        return this.getRestTemplate(objectMapper);
    }

    private RestTemplate getRestTemplate(ObjectMapper objectMapper) {
        RestTemplate restTemplate = new RestTemplate(this.clientHttpRequestFactory());
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
        restTemplate.setInterceptors(this.getInterceptors());
        restTemplate.setMessageConverters(getMessageConverters(objectMapper));
        return restTemplate;
    }

    @Bean
    public RestTemplate restTemplate(ObjectMapper objectMapper) {
        return this.getRestTemplate(objectMapper);
    }

    private List<ClientHttpRequestInterceptor> getInterceptors() {
        return List.of(new LogInterceptor());
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("poolScheduler");
        scheduler.setPoolSize(this.property.getMaxTotalConnections());
        return scheduler;
    }

    @Bean
    public Runnable idleConnectionMonitor(final PoolingHttpClientConnectionManager connectionManager) {
        return new Runnable() {
            @Override
            @Scheduled(fixedDelay = 10000)
            public void run() {
                try {
                    if (connectionManager != null) {
                        log.trace("run IdleConnectionMonitor - Closing expired and idle connections...");
                        connectionManager.closeExpiredConnections();
                        connectionManager.closeIdleConnections(RestAutoConfiguration.this.property.getCloseIdleConnectionWaitTimeSecs(),
                            TimeUnit.SECONDS);
                    } else {
                        log.trace("run IdleConnectionMonitor - Http Client Connection manager is not initialised");
                    }
                } catch (Exception e) {
                    log.error("run IdleConnectionMonitor - Exception occurred. msg={}", e.getMessage(), e);
                }
            }
        };
    }
}

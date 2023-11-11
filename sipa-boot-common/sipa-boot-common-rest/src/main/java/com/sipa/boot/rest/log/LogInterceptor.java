package com.sipa.boot.rest.log;

import static com.sipa.boot.core.constant.SipaConstant.FILTER_HEADER_KEYS;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.google.common.base.Stopwatch;
import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.core.tool.log.RestLogManager;

import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2023/5/22
 */
@Slf4j
public class LogInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {
        this.traceRequest(request);
        Stopwatch stopwatch = Stopwatch.createStarted();
        ClientHttpResponse response = execution.execute(request, body);
        this.traceResponse(request, stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000.0);
        return response;
    }

    private void traceRequest(HttpRequest request) {
        log.info("=========================== rest template request begin ===========================");
        RestLogManager.logStart(log, String.valueOf(request.getMethod()), null, getUrl(request),
            getHeaders(request.getHeaders()));
    }

    private static String getUrl(HttpRequest request) {
        return request.getURI().toString();
    }

    private static String getHeaders(HttpHeaders headers) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String key = entry.getKey();
            if (!StringUtils.containsAnyIgnoreCase(key, FILTER_HEADER_KEYS)) {
                List<String> values = entry.getValue();
                sb.append(key)
                    .append(SipaConstant.Symbol.EQUAL)
                    .append(String.join(SipaConstant.Symbol.JOIN, values))
                    .append(SipaConstant.Symbol.EMPTY);
            }
        }
        return sb.toString().trim();
    }

    private void traceResponse(HttpRequest request, double cost) {
        RestLogManager.logFinish(log, cost, String.valueOf(request.getMethod()), null, getUrl(request));
        log.info("============================ rest template request end ============================");
    }
}

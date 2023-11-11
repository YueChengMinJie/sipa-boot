package com.sipa.boot.es.property;

import java.util.List;

import com.sipa.boot.core.constant.TcpCloudConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author caszhou
 * @date 2021/10/16
 */
@Data
@ConfigurationProperties(prefix = TcpCloudConstant.Es.PREFIX)
public class ElasticSearchProperty {
    private List<String> uris;

    private String host = "172.19.65.94";

    private int port = 9200;

    private int connectionRequestTimeout = 5000;

    private int connectTimeout = 2000;

    private int socketTimeout = 2000;

    private int maxConnTotal = 30;

    public HttpHost[] createHosts() {
        if (CollectionUtils.isEmpty(uris)) {
            return new HttpHost[] {new HttpHost(host, port, "http")};
        }
        return uris.stream().map(HttpHost::create).toArray(HttpHost[]::new);
    }

    public RequestConfig.Builder applyRequestConfigBuilder(RequestConfig.Builder builder) {
        builder.setConnectTimeout(connectTimeout);
        builder.setConnectionRequestTimeout(connectionRequestTimeout);
        builder.setSocketTimeout(socketTimeout);
        return builder;
    }

    public HttpAsyncClientBuilder applyHttpAsyncClientBuilder(HttpAsyncClientBuilder builder) {
        builder.setMaxConnTotal(maxConnTotal);
        return builder;
    }
}

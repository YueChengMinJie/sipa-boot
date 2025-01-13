package com.sipa.boot.es;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.sipa.boot.core.constant.SipaBootConstant;

import lombok.Data;

/**
 * @author caszhou
 * @date 2021/10/16
 */
@Data
@ConfigurationProperties(prefix = SipaBootConstant.Es.PREFIX)
public class ElasticSearchProperty {
    private List<String> uris;

    private String host;

    private int port;

    private int connectionRequestTimeout = 5000;

    private int connectTimeout = 2000;

    private int socketTimeout = 2000;

    private int maxConnTotal = 30;

    public HttpHost[] createHosts() {
        if (CollectionUtils.isEmpty(this.uris)) {
            return new HttpHost[] {new HttpHost(this.host, this.port, "http")};
        }
        return this.uris.stream().map(HttpHost::create).toArray(HttpHost[]::new);
    }

    public RequestConfig.Builder applyRequestConfigBuilder(RequestConfig.Builder builder) {
        builder.setConnectTimeout(this.connectTimeout);
        builder.setConnectionRequestTimeout(this.connectionRequestTimeout);
        builder.setSocketTimeout(this.socketTimeout);
        return builder;
    }

    public HttpAsyncClientBuilder applyHttpAsyncClientBuilder(HttpAsyncClientBuilder builder) {
        builder.setMaxConnTotal(this.maxConnTotal);
        return builder;
    }
}

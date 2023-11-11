package com.sipa.boot.es;

import com.sipa.boot.es.client.ElasticRestClient;
import com.sipa.boot.es.property.ElasticSearchProperty;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author caszhou
 * @date 2021/10/16
 */
@Configuration
@ConditionalOnClass({ElasticSearchProperty.class})
@EnableConfigurationProperties({ElasticSearchProperty.class})
@AutoConfigureBefore(ElasticsearchRestClientAutoConfiguration.class)
public class ElasticSearchAutoConfiguration {
    private static ElasticSearchProperty property;

    public ElasticSearchAutoConfiguration(ElasticSearchProperty property) {
        ElasticSearchAutoConfiguration.property = property;
    }

    @Bean
    public RestClientBuilder elasticsearchRestClientBuilder() {
        return RestClient.builder(property.createHosts())
            .setRequestConfigCallback(property::applyRequestConfigBuilder)
            .setHttpClientConfigCallback(property::applyHttpAsyncClientBuilder);
    }

    @Bean
    public RestHighLevelClient elasticsearchRestHighLevelClient(RestClientBuilder restClientBuilder) {
        RestHighLevelClient client = new RestHighLevelClient(restClientBuilder);
        ElasticRestClient.setWriteClient(client);
        ElasticRestClient.setQueryClient(client);
        return client;
    }
}

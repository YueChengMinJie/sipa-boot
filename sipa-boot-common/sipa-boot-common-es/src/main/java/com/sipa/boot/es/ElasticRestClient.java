package com.sipa.boot.es;

import org.elasticsearch.client.RestHighLevelClient;

import lombok.Getter;

/**
 * @author caszhou
 * @date 2021/10/16
 **/
public class ElasticRestClient {
    @Getter
    private static RestHighLevelClient queryClient;

    @Getter
    private static RestHighLevelClient writeClient;

    private ElasticRestClient() {
        // prevent client
    }

    public static void setQueryClient(RestHighLevelClient queryClient) {
        ElasticRestClient.queryClient = queryClient;
    }

    public static void setWriteClient(RestHighLevelClient writeClient) {
        ElasticRestClient.writeClient = writeClient;
    }
}

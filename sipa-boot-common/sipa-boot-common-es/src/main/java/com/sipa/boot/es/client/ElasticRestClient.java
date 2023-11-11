package com.sipa.boot.es.client;

import org.elasticsearch.client.RestHighLevelClient;

/**
 * @author caszhou
 * @date 2021/10/16
 **/
public class ElasticRestClient {
    private static RestHighLevelClient queryClient;

    private static RestHighLevelClient writeClient;

    private ElasticRestClient() {
        // prevent client
    }

    public static RestHighLevelClient getQueryClient() {
        return queryClient;
    }

    public static void setQueryClient(RestHighLevelClient queryClient) {
        ElasticRestClient.queryClient = queryClient;
    }

    public static RestHighLevelClient getWriteClient() {
        return writeClient;
    }

    public static void setWriteClient(RestHighLevelClient writeClient) {
        ElasticRestClient.writeClient = writeClient;
    }
}

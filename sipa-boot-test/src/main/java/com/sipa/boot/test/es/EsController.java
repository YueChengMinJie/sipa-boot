// package com.sipa.boot.test.es;
//
// import java.util.Map;
//
// import com.sipa.boot.es.client.ElasticRestClient;
// import org.elasticsearch.action.index.IndexRequest;
// import org.elasticsearch.action.search.SearchRequest;
// import org.elasticsearch.action.search.SearchResponse;
// import org.elasticsearch.client.RequestOptions;
// import org.elasticsearch.common.xcontent.XContentType;
// import org.elasticsearch.index.query.QueryBuilders;
// import org.elasticsearch.search.builder.SearchSourceBuilder;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
//
// import cn.hutool.json.JSONUtil;
// import lombok.RequiredArgsConstructor;
// import lombok.SneakyThrows;
// import lombok.extern.slf4j.Slf4j;
// import ma.glasnost.orika.MapperFacade;
//
/// **
// * @author caszhou
// * @date 2023/8/10
// */
// @Slf4j
// @RestController
// @RequestMapping
// @RequiredArgsConstructor
// public class EsController {
// private static final String INDEX = "logan-20230920";
//
// private final MapperFacade mapperFacade;
//
// @SneakyThrows
// @GetMapping("/es/write")
// public void write() {
// IndexRequest indexRequest = new IndexRequest(INDEX);
// indexRequest.source(JSONUtil.toJsonStr(getLogan()), XContentType.JSON);
// ElasticRestClient.getWriteClient().index(indexRequest, RequestOptions.DEFAULT);
// }
//
// private static Logan getLogan() {
// return Logan.builder()
// .id(1)
// .taskId(1)
// .logType(1)
// .content("Hello Android Logan")
// .logTime(System.currentTimeMillis())
// .addTime("2023-09-15 13:47:48")
// .updateTime("2023-09-15 13:47:48")
// .build();
// }
//
// @SneakyThrows
// @PostMapping("/es/read")
// public Logan read() {
// SearchRequest searchRequest = new SearchRequest(INDEX);
// SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
// searchSourceBuilder.query(QueryBuilders.matchQuery("content", "hello"));
// searchRequest.source(searchSourceBuilder);
// SearchResponse response = ElasticRestClient.getQueryClient().search(searchRequest, RequestOptions.DEFAULT);
// return this.mapperFacade.map(response.getHits().getHits()[0].getSourceAsMap(), Logan.class);
// }
//
// @SneakyThrows
// @PostMapping("/es/read/map")
// public Map readMap() {
// SearchRequest searchRequest = new SearchRequest(INDEX);
// SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
// searchSourceBuilder.query(QueryBuilders.matchQuery("content", "hello"));
// searchRequest.source(searchSourceBuilder);
// SearchResponse response = ElasticRestClient.getQueryClient().search(searchRequest, RequestOptions.DEFAULT);
// Logan logan = this.mapperFacade.map(response.getHits().getHits()[0].getSourceAsMap(), Logan.class);
// return this.mapperFacade.map(logan, Map.class);
// }
// }

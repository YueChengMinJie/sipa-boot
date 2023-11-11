package com.sipa.boot.rest.util;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2022/5/19
 */
@Slf4j
@Component
public class LoadBalanceRestUtil {
    private static RestTemplate lbRestTemplate;

    public LoadBalanceRestUtil(RestTemplate lbRestTemplate) {
        LoadBalanceRestUtil.lbRestTemplate = lbRestTemplate;
    }

    /**
     * GET 请求
     *
     * @param url
     *            请求的 URL
     * @return 返回结果
     */
    public static Map<String, Object> get(String url) {
        return get(url, null, null, new ParameterizedTypeReference<>() {});
    }

    /**
     * GET 请求
     *
     * @param url
     *            请求的 URL
     * @param params
     *            请求参数
     * @return 返回结果
     */
    public static Map<String, Object> get(String url, Map<String, String> params) {
        return get(url, null, params, new ParameterizedTypeReference<>() {});
    }

    /**
     * GET 请求
     *
     * @param url
     *            请求的 URL
     * @param headers
     *            请求头
     * @return 返回结果
     */
    public static Map<String, Object> getWithHeader(String url, Map<String, String> headers) {
        return get(url, headers, null, new ParameterizedTypeReference<>() {});
    }

    /**
     * GET 请求
     *
     * @param url
     *            请求的 URL
     * @param headers
     *            请求头
     * @param params
     *            请求参数
     * @param cls
     *            返回数据的类型
     * @return 返回结果
     */
    public static <T> T get(String url, Map<String, String> headers, Map<String, String> params, Class<T> cls) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            headers.forEach(httpHeaders::add);
        }
        HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
        String queryString = generateQueryString(params);
        ResponseEntity<T> responseEntity =
            lbRestTemplate.exchange(url + "?" + queryString, HttpMethod.GET, httpEntity, cls);
        return responseEntity.getBody();
    }

    /**
     * GET 请求
     *
     * @param url
     *            请求的 URL
     * @param headers
     *            请求头
     * @param params
     *            请求参数
     * @param ptr
     *            返回数据的类型
     * @return 返回结果
     */
    public static <T> T get(String url, Map<String, String> headers, Map<String, String> params,
        ParameterizedTypeReference<T> ptr) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            headers.forEach(httpHeaders::add);
        }
        HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
        String queryString = generateQueryString(params);
        ResponseEntity<T> responseEntity =
            lbRestTemplate.exchange(url + "?" + queryString, HttpMethod.GET, httpEntity, ptr);
        return responseEntity.getBody();
    }

    /**
     * POST 请求
     *
     * @param url
     *            请求的 URL
     * @param payload
     *            请求参数
     * @return 返回结果
     */
    public static Map<String, Object> post(String url, Object payload) {
        return post(url, null, payload, new ParameterizedTypeReference<>() {});
    }

    /**
     * POST 请求
     *
     * @param url
     *            请求的 URL
     * @param headers
     *            请求头
     * @param payload
     *            请求参数
     * @param cls
     *            返回数据的类型
     * @return 返回结果
     */
    public static <T> T post(String url, Map<String, String> headers, Object payload, Class<T> cls) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            headers.forEach(httpHeaders::add);
        }
        MediaType mediaType = MediaType.APPLICATION_JSON;
        httpHeaders.setContentType(mediaType);
        HttpEntity<Object> httpEntity = new HttpEntity<>(payload, httpHeaders);
        ResponseEntity<T> responseEntity = lbRestTemplate.exchange(url, HttpMethod.POST, httpEntity, cls);
        return responseEntity.getBody();
    }

    /**
     * POST 请求
     *
     * @param url
     *            请求的 URL
     * @param headers
     *            请求头
     * @param payload
     *            请求参数
     * @param ptr
     *            返回数据的类型
     * @return 返回结果
     */
    public static <T> T post(String url, Map<String, String> headers, Object payload,
        ParameterizedTypeReference<T> ptr) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            headers.forEach(httpHeaders::add);
        }
        MediaType mediaType = MediaType.APPLICATION_JSON;
        httpHeaders.setContentType(mediaType);
        HttpEntity<Object> httpEntity = new HttpEntity<>(payload, httpHeaders);
        ResponseEntity<T> responseEntity = lbRestTemplate.exchange(url, HttpMethod.POST, httpEntity, ptr);
        return responseEntity.getBody();
    }

    /**
     * POST 请求 请求参数使用form类型
     *
     * @param url
     *            请求的 URL
     * @param headers
     *            请求头
     * @param payload
     *            请求参数
     * @param cls
     *            返回数据的类型
     * @return 返回结果
     */
    public static <T> T postForm(String url, Map<String, String> headers, Object payload, Class<T> cls) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            headers.forEach(httpHeaders::add);
        }
        MediaType mediaType = MediaType.APPLICATION_FORM_URLENCODED;
        httpHeaders.setContentType(mediaType);
        HttpEntity<Object> httpEntity = new HttpEntity<>(payload, httpHeaders);
        ResponseEntity<T> responseEntity = lbRestTemplate.exchange(url, HttpMethod.POST, httpEntity, cls);
        return responseEntity.getBody();
    }

    /**
     * PUT 请求
     *
     * @param url
     *            请求的 URL
     * @param headers
     *            请求头
     * @param payload
     *            请求参数
     * @param cls
     *            返回数据的类型
     * @return 返回结果
     */
    public static <T> T put(String url, Map<String, String> headers, Object payload, Class<T> cls) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            headers.forEach(httpHeaders::add);
        }
        MediaType mediaType = MediaType.APPLICATION_JSON;
        httpHeaders.setContentType(mediaType);
        HttpEntity<Object> httpEntity = new HttpEntity<>(payload, httpHeaders);
        ResponseEntity<T> responseEntity = lbRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, cls);
        return responseEntity.getBody();
    }

    /**
     * DELETE 请求
     *
     * @param url
     *            请求的 URL
     * @param cls
     *            返回数据的类型
     * @return 返回结果
     */
    public static <T> T delete(String url, Class<T> cls) {
        return delete(url, null, null, cls);
    }

    /**
     * DELETE 请求
     *
     * @param url
     *            请求的 URL
     * @param headers
     *            请求头
     * @param params
     *            请求参数
     * @param cls
     *            返回数据的类型
     * @return 返回结果
     */
    public static <T> T delete(String url, Map<String, String> headers, Map<String, String> params, Class<T> cls) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            headers.forEach(httpHeaders::add);
        }
        HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
        String queryString = generateQueryString(params);
        ResponseEntity<T> responseEntity =
            lbRestTemplate.exchange(url + "?" + queryString, HttpMethod.DELETE, httpEntity, cls);
        return responseEntity.getBody();
    }

    /**
     * 将 Map 转换成查询字符串
     *
     * @param params
     *            参数
     * @return 查询字符串
     */
    private static String generateQueryString(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        params.forEach((key, value) -> {
            stringBuilder.append(key).append("=").append(value).append("&");
        });
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }
}

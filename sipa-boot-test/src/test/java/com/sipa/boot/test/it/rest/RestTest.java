package com.sipa.boot.test.it.rest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.junit4.SpringRunner;

import com.sipa.boot.core.exception.system.SystemRuntimeException;
import com.sipa.boot.rest.util.RestUtil;
import com.sipa.boot.test.SipaTestApplication;

/**
 * 报错修复
 *
 * @author zhouxiajie
 * @date 2021/1/27
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SipaTestApplication.class)
public class RestTest {
    @Test
    @Ignore
    public void testGet() {
        String url = "https://example.com/api/users";
        Map<String, String> headers = new HashMap<>(16);
        headers.put("Authorization", "Bearer access_token");
        Map<String, String> params = new HashMap<>(16);
        params.put("page", "1");
        params.put("per_page", "10");
        assertThatThrownBy(
            () -> RestUtil.get(url, headers, params, new ParameterizedTypeReference<List<Object>>() {}.getClass()))
                .isInstanceOf(SystemRuntimeException.class)
                .hasMessageContaining("ErrorInfo(code=000005, msg=调用外部服务错误)");
    }

    @Test
    @Ignore
    public void testPost() {
        String url = "https://example.com/api/users";
        Map<String, String> headers = new HashMap<>(16);
        headers.put("Authorization", "Bearer access_token");
        Map<String, Object> payload = new HashMap<>(16);
        payload.put("name", "Alice");
        payload.put("email", "alice@example.com");
        assertThatThrownBy(() -> RestUtil.post(url, headers, payload, Object.class))
            .isInstanceOf(SystemRuntimeException.class)
            .hasMessageContaining("ErrorInfo(code=000005, msg=调用外部服务错误)");
    }

    @Test
    @Ignore
    public void testPut() {
        String url = "https://example.com/api/users/1";
        Map<String, String> headers = new HashMap<>(16);
        headers.put("Authorization", "Bearer access_token");
        Map<String, Object> payload = new HashMap<>(16);
        payload.put("name", "Bob");
        payload.put("email", "bob@example.com");
        assertThatThrownBy(() -> RestUtil.put(url, headers, payload, Object.class))
            .isInstanceOf(SystemRuntimeException.class)
            .hasMessageContaining("ErrorInfo(code=000005, msg=调用外部服务错误)");
    }

    @Test
    @Ignore
    public void testDelete() {
        String url = "https://example.com/api/users/1";
        Map<String, String> headers = new HashMap<>(16);
        headers.put("Authorization", "Bearer access_token");
        Map<String, String> params = new HashMap<>(16);
        assertThatThrownBy(() -> RestUtil.delete(url, headers, params, null)).isInstanceOf(SystemRuntimeException.class)
            .hasMessageContaining("ErrorInfo(code=000005, msg=调用外部服务错误)");
    }
}

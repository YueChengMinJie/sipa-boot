package com.sipa.boot.test.it.secure;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sipa.boot.test.SipaTestApplication;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhouxiajie
 * @date 2021/1/27
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SipaTestApplication.class)
public class SecureTest {
    @Test
    @Ignore
    public void testLogin() {
        StpUtil.login(1L);
        log.info(JSONUtil.toJsonPrettyStr(StpUtil.getTokenInfo()));
    }
}

package com.sipa.boot.test.it.test;

import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sipa.boot.test.SipaTestApplication;
import com.sipa.boot.test.test.NormalService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2023/9/7
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SipaTestApplication.class)
public class MockitoTest {
    @Mock
    private NormalService normalService;

    @Test
    public void testMockito() {
        when(this.normalService.test()).thenReturn("Hello Mockito");
        log.info(this.normalService.test());
    }
}

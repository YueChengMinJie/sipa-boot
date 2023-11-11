package com.sipa.boot.test.test;

import org.springframework.stereotype.Service;

/**
 * @author caszhou
 * @date 2023/9/7
 */
@Service
public class NormalService {
    public String test() {
        return "Normal service";
    }
}

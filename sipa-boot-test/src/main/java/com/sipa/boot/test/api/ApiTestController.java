package com.sipa.boot.test.api;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sipa.boot.mvc.codec.annotation.Encrypt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2023/8/10
 */
@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class ApiTestController {
    @Encrypt
    @PostMapping("/api/test")
    public String test(@RequestBody Map<String, String> map) {
        return map.get("hello") + " " + map.get("world") + "!";
    }

    @Encrypt
    @PostMapping("/api/test2")
    public Map<String, String> test2(@RequestBody Map<String, String> map) {
        return map;
    }

    @PostMapping("/api/test3")
    public String test3(@RequestBody Map<String, String> map) {
        return map.get("hello") + " " + map.get("world") + "!";
    }

    @PostMapping("/api/test4")
    public Map<String, String> test4(@RequestBody Map<String, String> map) {
        return map;
    }

    @PostMapping("/api/test5")
    public Map<String, String> test5(@RequestBody Map<String, String> map) {
        return map;
    }
}

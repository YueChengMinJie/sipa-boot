package com.sipa.boot.cache.service;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.sipa.boot.cache.mapper.CacheMapper;
import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.core.service.CacheService;

import lombok.RequiredArgsConstructor;

/**
 * @author caszhou
 * @date 2025/1/22
 */
@Component
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {
    private final CacheMapper cacheMapper;

    @Override
    public boolean exist(String key) {
        return !cacheMapper.setIfAbsent(key, SipaConstant.StringValue.STRING_VALUE_1,
            TimeUnit.HOURS.toSeconds(SipaConstant.Number.INT_2));
    }
}

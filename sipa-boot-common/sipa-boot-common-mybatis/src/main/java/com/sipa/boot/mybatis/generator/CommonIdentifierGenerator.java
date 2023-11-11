package com.sipa.boot.mybatis.generator;

import com.sipa.boot.core.tool.uid.UidUtil;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;

/**
 * @author caszhou
 * @date 2023/4/23
 */
@Component
public class CommonIdentifierGenerator implements IdentifierGenerator {
    @Override
    public Number nextId(Object entity) {
        return UidUtil.nextLid();
    }
}

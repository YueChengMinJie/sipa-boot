package com.sipa.boot.mybatis.log;

import org.apache.ibatis.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sipa.boot.core.env.EnvConstant;
import com.sipa.boot.core.env.EnvProvider;

import cn.hutool.core.util.StrUtil;
import lombok.NoArgsConstructor;

/**
 * @author caszhou
 * @date 2023/6/9
 */
@NoArgsConstructor
public class CustomSlf4jImpl implements Log {
    private Logger log = LoggerFactory.getLogger(CustomSlf4jImpl.class);

    public CustomSlf4jImpl(String name) {
        this.log = LoggerFactory.getLogger(name);
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void error(String s, Throwable throwable) {
        if (StrUtil.equals(EnvProvider.getEnv(), EnvConstant.ENV_LOCAL)) {
            log.error(s, throwable);
        } else {
            System.err.println(s);
            throwable.printStackTrace(System.err);
        }
    }

    @Override
    public void error(String s) {
        if (StrUtil.equals(EnvProvider.getEnv(), EnvConstant.ENV_LOCAL)) {
            System.err.println(s);
        } else {
            log.error(s);
        }
    }

    @Override
    public void debug(String s) {
        if (StrUtil.equals(EnvProvider.getEnv(), EnvConstant.ENV_LOCAL)) {
            System.out.println(s);
        } else {
            log.info(s);
        }
    }

    @Override
    public void trace(String s) {
        if (StrUtil.equals(EnvProvider.getEnv(), EnvConstant.ENV_LOCAL)) {
            System.out.println(s);
        } else {
            log.info(s);
        }
    }

    @Override
    public void warn(String s) {
        if (StrUtil.equals(EnvProvider.getEnv(), EnvConstant.ENV_LOCAL)) {
            System.out.println(s);
        } else {
            log.warn(s);
        }
    }
}

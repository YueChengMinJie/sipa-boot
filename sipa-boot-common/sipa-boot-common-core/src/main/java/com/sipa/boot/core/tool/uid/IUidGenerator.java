package com.sipa.boot.core.tool.uid;

/**
 * @author caszhou
 * @date 2019-05-13
 */
public interface IUidGenerator {
    Long nextLid();

    String nextSid();
}

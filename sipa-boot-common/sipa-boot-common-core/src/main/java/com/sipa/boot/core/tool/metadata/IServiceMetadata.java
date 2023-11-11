package com.sipa.boot.core.tool.metadata;

/**
 * @author caszhou
 * @date 2023/4/23
 */
public interface IServiceMetadata {
    IServiceMetadata DEFAULT = new DefaultServiceMetadata();

    String getAppName();

    String getIp();

    String getPort();

    int getWorkId();
}

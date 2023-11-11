package com.sipa.boot.extension;

import java.util.Map;

/**
 * @author caszhou
 * @date 2023/6/6
 */
public interface ExtensionRepository {
    Map<ExtensionCoordinate<?>, ExtensionPoint> getExtensionRepo();
}

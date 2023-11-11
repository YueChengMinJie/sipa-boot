package com.sipa.boot.extension;

import java.util.HashMap;
import java.util.Map;

/**
 * @author caszhou
 * @date 2019/4/24
 */
public class DefaultExtensionRepository implements ExtensionRepository {
    private final Map<ExtensionCoordinate<?>, ExtensionPoint> extensionRepo = new HashMap<>(16);

    @Override
    public Map<ExtensionCoordinate<?>, ExtensionPoint> getExtensionRepo() {
        return extensionRepo;
    }
}

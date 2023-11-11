package com.sipa.boot.extension;

/**
 * ExtensionPointI is the parent interface of all ExtensionPoints
 * 扩展点表示一块逻辑在不同的业务有不同的实现，使用扩展点做接口申明，然后用Extension（扩展）去实现扩展点。
 *
 * @author caszhou
 * @date 2019/4/24
 */
public interface ExtensionPoint {
    default boolean isDefault() {
        return false;
    }
}

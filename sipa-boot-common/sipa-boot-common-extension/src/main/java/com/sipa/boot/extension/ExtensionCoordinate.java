package com.sipa.boot.extension;

import lombok.Getter;

/**
 * Extension Coordinate(扩展坐标) is used to uniquely position an Extension
 *
 * @author caszhou
 * @date 2019/4/24
 */
public class ExtensionCoordinate<E> {
    @Getter
    private Class<E> extensionPointClass;

    private final String extensionPointName;

    @Getter
    private BizScenario bizScenario;

    private final String bizScenarioUniqueIdentity;

    public ExtensionCoordinate(Class<E> extPtClass, BizScenario bizScenario) {
        this.extensionPointClass = extPtClass;
        this.extensionPointName = extPtClass.getName();
        this.bizScenario = bizScenario;
        this.bizScenarioUniqueIdentity = bizScenario.getUniqueIdentity();
    }

    public ExtensionCoordinate(String extensionPoint, String bizScenario) {
        this.extensionPointName = extensionPoint;
        this.bizScenarioUniqueIdentity = bizScenario;
    }

    public static <T> ExtensionCoordinate<T> valueOf(Class<T> extPtClass, BizScenario bizScenario) {
        return new ExtensionCoordinate<>(extPtClass, bizScenario);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bizScenarioUniqueIdentity == null) ? 0 : bizScenarioUniqueIdentity.hashCode());
        result = prime * result + ((extensionPointName == null) ? 0 : extensionPointName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ExtensionCoordinate<?> other = (ExtensionCoordinate<?>)obj;
        if (bizScenarioUniqueIdentity == null) {
            if (other.bizScenarioUniqueIdentity != null) {
                return false;
            }
        } else if (!bizScenarioUniqueIdentity.equals(other.bizScenarioUniqueIdentity)) {
            return false;
        }
        if (extensionPointName == null) {
            return other.extensionPointName == null;
        } else {
            return extensionPointName.equals(other.extensionPointName);
        }
    }

    @Override
    public String toString() {
        return "ExtensionCoordinate [extensionPointName=" + extensionPointName + ", bizScenarioUniqueIdentity="
            + bizScenarioUniqueIdentity + "]";
    }
}

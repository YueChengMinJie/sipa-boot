package com.sipa.boot.nacos.env;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import org.apache.commons.lang3.StringUtils;

/**
 * @author caszhou
 * @date 2022-09-14
 */
public class NacosVersion {
    public static final String UNKNOWN = "unknown";

    public static String getNacosVersion() {
        // 高版本Nacos
        String nacosVersion = getNacosVersion("com.alibaba.nacos.common.utils.VersionUtils", "version");
        if (!StringUtils.equals(nacosVersion, UNKNOWN)) {
            return nacosVersion;
        }

        nacosVersion = getNacosVersion("com.alibaba.nacos.common.utils.VersionUtils", "VERSION");
        if (!StringUtils.equals(nacosVersion, UNKNOWN)) {
            return nacosVersion;
        }

        // 低版本Nacos
        nacosVersion = getNacosVersion("com.alibaba.nacos.common.util.VersionUtils", "version");
        if (!StringUtils.equals(nacosVersion, UNKNOWN)) {
            return nacosVersion;
        }

        nacosVersion = getNacosVersion("com.alibaba.nacos.common.util.VersionUtils", "VERSION");
        if (!StringUtils.equals(nacosVersion, UNKNOWN)) {
            return nacosVersion;
        }

        return null;
    }

    private static String getNacosVersion(String className, String versionName) {
        try {
            Class<?> clazz = Class.forName(className);
            Field field = clazz.getField(versionName);

            return field.get(versionName).toString();
        } catch (Exception e) {
            return UNKNOWN;
        }
    }

    public static String getVersion(String className) {
        try {
            Class<?> clazz = Class.forName(className);

            return getVersion(clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getVersion(Class<?> clazz) {
        String implementationVersion = clazz.getPackage().getImplementationVersion();
        if (implementationVersion != null) {
            return implementationVersion;
        }

        CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
        if (codeSource == null) {
            return null;
        }

        URL codeSourceLocation = codeSource.getLocation();
        try {
            URLConnection connection = codeSourceLocation.openConnection();
            if (connection instanceof JarURLConnection) {
                return getImplementationVersion(((JarURLConnection)connection).getJarFile());
            }
            try (JarFile jarFile = new JarFile(new File(codeSourceLocation.toURI()))) {
                return getImplementationVersion(jarFile);
            }
        } catch (Exception ex) {
            return null;
        }
    }

    private static String getImplementationVersion(JarFile jarFile) throws IOException {
        return jarFile.getManifest().getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
    }
}
package com.sipa.boot.storage.util;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.core.util.SipaUtil;

/**
 * @author caszhou
 * @date 2022/12/23
 */
public class PathUtil {
    /**
     * 获取父路径
     */
    public static String getParent(String path) {
        if (path.endsWith(SipaConstant.Symbol.SLASH) || path.endsWith(SipaConstant.Symbol.BACKSLASH)) {
            path = path.substring(0, path.length() - 1);
        }
        int endIndex =
            Math.max(path.lastIndexOf(SipaConstant.Symbol.SLASH), path.lastIndexOf(SipaConstant.Symbol.BACKSLASH));
        return endIndex > -1 ? path.substring(0, endIndex) : null;
    }

    /**
     * 合并路径
     */
    public static String join(String... paths) {
        StringBuilder sb = new StringBuilder();
        for (String path : paths) {
            String left = sb.toString();
            boolean leftHas = left.endsWith(SipaConstant.Symbol.SLASH) || left.endsWith(SipaConstant.Symbol.BACKSLASH);
            boolean rightHas = path.endsWith(SipaConstant.Symbol.SLASH) || path.endsWith(SipaConstant.Symbol.BACKSLASH);

            if (leftHas && rightHas) {
                sb.append(path.substring(1));
            } else if (!left.isEmpty() && !leftHas && !rightHas) {
                sb.append(SipaConstant.Symbol.SLASH).append(path);
            } else {
                sb.append(path);
            }
        }
        return sb.toString();
    }

    public static String combine(Object... paths) {
        return StringUtils.join(Arrays.stream(paths)
            .map(SipaUtil::stringValueOf)
            .filter(StringUtils::isNotBlank)
            .map(s -> StringUtils.strip(s, SipaConstant.Symbol.SLASH))
            .collect(Collectors.toList()), SipaConstant.Symbol.SLASH);
    }
}

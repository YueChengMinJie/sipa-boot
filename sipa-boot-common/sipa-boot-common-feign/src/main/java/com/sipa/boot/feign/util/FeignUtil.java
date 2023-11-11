package com.sipa.boot.feign.util;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author caszhou
 * @date 2023/8/16
 */
public class FeignUtil {
    public static String getHeader(String key) {
        ServletRequestAttributes requestAttributes =
            (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(requestAttributes)) {
            HttpServletRequest request = requestAttributes.getRequest();
            return request.getHeader(key);
        }
        return null;
    }
}

package com.sipa.boot.core.tool.uid;

import org.springframework.stereotype.Component;

/**
 * spring容器中使用.
 * 
 * @author caszhou
 * @date 2020/4/23
 */
@Component
public class UidUtil {
    private static IUidGenerator uidGenerator;

    public UidUtil(IUidGenerator uidGenerator) {
        UidUtil.uidGenerator = uidGenerator;
    }

    public static Long nextLid() {
        return uidGenerator.nextLid();
    }

    public static String nextSid() {
        return uidGenerator.nextSid();
    }
}

package com.sipa.boot.test.it.extension.materials.customer.app.extension;

import java.util.HashMap;

import com.sipa.boot.extension.Extension;
import com.sipa.boot.extension.Extensions;
import com.sipa.boot.test.it.extension.materials.customer.app.extensionpoint.StatusNameConvertorExtPt;

/**
 * @author caszhou
 * @date 2019/4/24
 */
@Extensions(bizId = {"Samsung", "Motorola"}, useCase = {"order", "parts"}, scenario = {"scenario1", "scenario2"},
    value = {@Extension(bizId = "Sony", useCase = "user", scenario = "scenario3"),
        @Extension(bizId = "Siemens", useCase = "order", scenario = "scenario1")})
public class StatusNameConvertorExt implements StatusNameConvertorExtPt {
    /**
     * In real business scenarios, the business status is usually represented by numbers. In some places, the number
     * needs to be mapped to the real status name. For ease of understanding here, the status name is the number in
     * English
     */
    HashMap<Integer, String> map = new HashMap<>() {
        private static final long serialVersionUID = 8774884996455662944L;

        {
            put(1, "one");
            put(2, "two");
            put(3, "three");
            put(4, "four");
            put(5, "five");
            put(6, "six");
            put(7, "seven");
            put(8, "eight");
        }
    };

    @Override
    public String statusNameConvertor(Integer statusCode) {
        return map.getOrDefault(statusCode, "unknown");
    }
}

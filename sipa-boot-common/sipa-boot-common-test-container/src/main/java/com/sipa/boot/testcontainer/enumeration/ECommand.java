package com.sipa.boot.testcontainer.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author caszhou
 * @date 2019/4/24
 */
@Getter
@AllArgsConstructor
public enum ECommand {
    // method
    TestMethodRunCmd("", "[^\\s]*[\\(#].*"),

    // class
    TestClassRunCmd("", "(\\w+\\.\\w+){1,}"),

    // guide
    GuideCmd("", "^[rhq]$"),

    ;

    private final String cmd;

    private final String desc;
}

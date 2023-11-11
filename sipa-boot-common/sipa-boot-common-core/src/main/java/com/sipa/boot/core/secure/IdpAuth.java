package com.sipa.boot.core.secure;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import lombok.*;

/**
 * @author caszhou
 * @date 2019-05-08
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdpAuth {
    private String url;

    private String code;

    private Long applicationId;

    public boolean notNull() {
        return StringUtils.isNotBlank(url) && StringUtils.isNotBlank(code) && Objects.nonNull(applicationId);
    }
}

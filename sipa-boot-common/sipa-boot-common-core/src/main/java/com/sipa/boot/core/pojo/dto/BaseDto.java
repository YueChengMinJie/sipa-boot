package com.sipa.boot.core.pojo.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author caszhou
 * @date 2023/4/23
 */
@Getter
@Setter
public class BaseDto<T> {
    private T id;
}

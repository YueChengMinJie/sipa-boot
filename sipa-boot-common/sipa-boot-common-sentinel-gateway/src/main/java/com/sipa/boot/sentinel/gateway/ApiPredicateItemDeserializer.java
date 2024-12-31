package com.sipa.boot.sentinel.gateway;

import java.io.IOException;

import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * @author caszhou
 * @date 2024/12/31
 */
public class ApiPredicateItemDeserializer extends JsonDeserializer<ApiPredicateItem> {
    @Override
    public ApiPredicateItem deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException, JacksonException {
        return p.readValueAs(ApiPathPredicateItem.class);
    }
}

package com.sipa.boot.core.jackson.deserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.sipa.boot.core.constant.SipaConstant;

import cn.hutool.core.date.LocalDateTimeUtil;

/**
 * @author caszhou
 * @date 2023/8/10
 */
public class MillisOrLocalDateTimeDeserializer extends LocalDateTimeDeserializer {
    private static final long serialVersionUID = -1897250800982016590L;

    public MillisOrLocalDateTimeDeserializer(DateTimeFormatter dateTimeFormatter) {
        super(dateTimeFormatter);
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            long ts = parser.getValueAsLong();
            String sTs = String.valueOf(ts);
            if (StringUtils.length(sTs) == SipaConstant.Number.INT_10) {
                ts = Long.parseLong(
                    sTs + StringUtils.repeat(SipaConstant.StringValue.STRING_VALUE_0, SipaConstant.Number.INT_3));
            }
            return LocalDateTimeUtil.of(ts);
        }

        return super.deserialize(parser, context);
    }
}

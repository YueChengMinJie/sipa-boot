package com.sipa.boot.core.jackson.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author caszhou
 * @date 2019/8/28
 */
public class LongSerializer extends JsonSerializer<Long> {
    public static final LongSerializer INSTANCE = new LongSerializer();

    private static final Long MAX = 9007199254740991L;

    private LongSerializer() {}

    @Override
    public void serialize(Long value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        if (value > MAX) {
            jsonGenerator.writeString(value.toString());
        } else {
            jsonGenerator.writeNumber(value);
        }
    }
}

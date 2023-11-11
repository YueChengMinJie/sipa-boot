package com.sipa.boot.core.enumeration.serializer;

import java.io.IOException;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author caszhou
 * @date 2019/8/28
 */
public class EnumSerializer extends JsonSerializer<Enum> {
    public static final EnumSerializer INSTANCE = new EnumSerializer();

    private EnumSerializer() {
    }

    @Override
    public void serialize(Enum e, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        if (e != null && IEnum.class.isAssignableFrom(e.getClass())) {
            IEnum ie = (IEnum)e;
            Serializable value = ie.getValue();
            if (value instanceof Integer) {
                jsonGenerator.writeNumber((Integer)value);
            } else if (value instanceof String) {
                jsonGenerator.writeString((String)value);
            } else {
                jsonGenerator.writeString(String.valueOf(value));
            }
        } else if (e != null) {
            jsonGenerator.writeNumber(e.ordinal());
        }
    }
}

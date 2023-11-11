package com.sipa.boot.core.enumeration.deseriallzer;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

/**
 * @author caszhou
 * @date 2019-01-22
 */
public class EnumDeserializer extends JsonDeserializer<Enum> implements ContextualDeserializer {
    public static final EnumDeserializer INSTANCE = new EnumDeserializer();

    private EnumDeserializer() {
    }

    private Class<Enum> enumCls;

    @Override
    public Enum deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
        return getEnum(enumCls, parser.getText()).orElse(null);
    }

    private Optional<Enum> getEnum(Class<Enum> enumCls, String text) {
        Enum[] enumConstants = enumCls.getEnumConstants();
        if (IEnum.class.isAssignableFrom(enumCls)) {
            for (Enum e : enumConstants) {
                IEnum ie = (IEnum)e;
                if (String.valueOf(ie.getValue()).equals(text)) {
                    return Optional.of(e);
                }
            }
        } else if (StringUtils.isNumeric(text)) {
            int val = Integer.parseInt(text);
            if (val < enumConstants.length) {
                return Optional.of(enumConstants[val]);
            }
        } else {
            for (Enum e : enumConstants) {
                if (StringUtils.equalsIgnoreCase(e.name(), text)) {
                    return Optional.of(e);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public JsonDeserializer createContextual(DeserializationContext ctx, BeanProperty property) {
        Class rawCls = ctx.getContextualType().getRawClass();
        Class<Enum> enumCls = (Class<Enum>)rawCls;
        EnumDeserializer clone = new EnumDeserializer();
        clone.setEnumCls(enumCls);
        return clone;
    }

    private void setEnumCls(Class<Enum> enumCls) {
        this.enumCls = enumCls;
    }
}

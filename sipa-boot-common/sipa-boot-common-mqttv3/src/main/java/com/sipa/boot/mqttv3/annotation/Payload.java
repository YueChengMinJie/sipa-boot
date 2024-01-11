package com.sipa.boot.mqttv3.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.convert.converter.Converter;

/**
 * Message content.
 * <p>
 * If there is no such annotation in the parameter list, the default custom type has this annotation.
 * <p>
 * If there is this annotation in the parameter list, only the message content will be assigned to the annotated
 * parameter.
 * 
 * @author caszhou
 * @date 2022/6/23
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Payload {
    /**
     * The processing before conversion is executed sequentially, starting from byte[] and ending with the target type.
     * <p>
     * If the result is the same as the target type after the execution in sequence, it is directly assigned, if it is
     * different, MqttConversionService is called for conversion.
     *
     * @return Converter
     */
    Class<? extends Converter<?, ?>>[] value() default {};

    /**
     * if required is true and value is null, method does not execute.
     * 
     * @return boolean
     */
    boolean required() default false;
}

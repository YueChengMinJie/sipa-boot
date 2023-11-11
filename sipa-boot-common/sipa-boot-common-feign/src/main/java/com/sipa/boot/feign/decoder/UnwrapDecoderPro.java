package com.sipa.boot.feign.decoder;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipa.boot.core.response.ResponseWrapper;

import cn.hutool.http.HttpStatus;
import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;

/**
 * @author caszhou
 * @date 2019/3/1
 */
public class UnwrapDecoderPro extends UnwrapDecoder {
    private final Decoder delegate;

    public UnwrapDecoderPro(ObjectMapper objectMapper, Decoder delegate) {
        super(objectMapper);
        Objects.requireNonNull(delegate, "Decoder must not be null");
        this.delegate = delegate;
    }

    @Override
    @Nullable
    public Object decode(Response response, Type type) throws IOException, FeignException {
        if (!shouldUnPackage(type)) {
            return delegate.decode(response, type);
        }
        if (response.status() == HttpStatus.HTTP_NOT_FOUND || response.status() == HttpStatus.HTTP_NO_CONTENT) {
            return null;
        }
        return super.decode(response, type);
    }

    private boolean shouldUnPackage(Type type) {
        if (type instanceof ParameterizedType) {
            return true;
        }
        return !((Class<?>)type).isAnnotationPresent(NoUnPackage.class);
    }

    @Override
    protected boolean checkWrapper(ResponseWrapper<?> rw) {
        return super.checkWrapper(rw) && StringUtils.isNotBlank(rw.getErrorCode());
    }
}

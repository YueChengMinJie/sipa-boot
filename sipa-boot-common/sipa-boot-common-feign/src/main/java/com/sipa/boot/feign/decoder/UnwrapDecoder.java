package com.sipa.boot.feign.decoder;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Objects;

import org.springframework.lang.Nullable;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.sipa.boot.core.response.ResponseWrapper;

import cn.hutool.http.HttpStatus;
import feign.FeignException;
import feign.Request;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import lombok.RequiredArgsConstructor;

/**
 * @author caszhou
 * @date 2019/3/1
 */
@RequiredArgsConstructor
public class UnwrapDecoder implements Decoder {
    private static final int SUCCESS_CODE = 0;

    private final ObjectMapper objectMapper;

    @Override
    @Nullable
    public Object decode(Response response, Type type) throws IOException, FeignException {
        Reader reader = response.body().asReader(Charset.defaultCharset());
        ResponseWrapper<?> rw = this.objectMapper.readValue(reader, ResponseWrapper.class);
        Request request = response.request();
        if (this.checkWrapper(rw)) {
            int code = rw.getCode();
            if (code == SUCCESS_CODE) {
                Object data = rw.getData();
                if (this.checkData(data)) {
                    JavaType javaType = TypeFactory.defaultInstance().constructType(type);
                    return this.objectMapper.convertValue(data, javaType);
                } else {
                    return null;
                }
            } else {
                throw new DecodeException(HttpStatus.HTTP_OK, rw.getMsg(), request);
            }
        } else {
            throw new DecodeException(HttpStatus.HTTP_OK, "Response body is empty", request);
        }
    }

    protected boolean checkWrapper(ResponseWrapper<?> rw) {
        return Objects.nonNull(rw);
    }

    protected boolean checkData(Object data) {
        return Objects.nonNull(data);
    }
}

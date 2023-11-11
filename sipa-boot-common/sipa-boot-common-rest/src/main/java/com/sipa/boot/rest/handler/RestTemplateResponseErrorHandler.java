package com.sipa.boot.rest.handler;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

import java.io.IOException;
import java.nio.charset.Charset;

import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2023/4/24
 */
@Slf4j
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return (response.getStatusCode().series() == CLIENT_ERROR || response.getStatusCode().series() == SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        log.info(IOUtils.toString(response.getBody(), Charset.defaultCharset()));
        if (response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.HTTP_SERVER_ERROR);
        } else if (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.HTTP_CLIENT_ERROR);
        }
    }
}

package com.sipa.boot.mvc.codec.advice;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import com.sipa.boot.core.exception.system.SystemRuntimeException;
import com.sipa.boot.core.service.CacheService;
import com.sipa.boot.core.util.SipaUtil;
import com.sipa.boot.mvc.codec.property.ApiProperty;
import com.sipa.boot.mvc.codec.util.ApiUtil;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import lombok.extern.slf4j.Slf4j;

/**
 * 解码处理
 *
 * @author caszhou
 * @date 2020/9/18
 */
@Slf4j
public class DecodeInputMessage implements HttpInputMessage {
    private final HttpHeaders headers;

    private InputStream body;

    public DecodeInputMessage(HttpInputMessage httpInputMessage, ApiProperty apiProperty, CacheService cacheService) {
        // 这里是body 读取之前的处理
        this.headers = httpInputMessage.getHeaders();

        String encodeAesKey;
        List<String> keys = this.headers.get(SipaConstant.API_KEY);
        SystemRuntimeException ex = SystemExceptionFactory.bizException(ESystemErrorCode.CANNOT_FIND_KEY);
        if (CollectionUtils.isNotEmpty(keys)) {
            encodeAesKey = SipaUtil.first(keys).orElseThrow(() -> ex);
        } else {
            throw ex;
        }

        try {
            // 1、解码得到aes 密钥
            RSA rsa = new RSA(ApiUtil.cleanKey(apiProperty.getRsaPrivateKey()),
                ApiUtil.cleanKey(apiProperty.getRsaPublicKey()));
            byte[] decodeAesKey = rsa.decrypt(Base64.decode(encodeAesKey), KeyType.PrivateKey);
            String uuid = new String(decodeAesKey);
            // 防
            if (Objects.nonNull(cacheService) && cacheService.exist(uuid)) {
                throw SystemExceptionFactory.bizException(ESystemErrorCode.NOT_FOUND);
            }
            // 2、从inputStreamReader 得到aes 加密的内容
            String encodeAesContent = new BufferedReader(new InputStreamReader(httpInputMessage.getBody())).lines()
                .collect(Collectors.joining(System.lineSeparator()));
            if (StringUtils.isNotBlank(encodeAesContent)) {
                // 3、AES通过密钥CBC解码
                String aesDecode = SecureUtil.aes(decodeAesKey).decryptStr(Base64.decode(encodeAesContent));
                if (StringUtils.isNotBlank(aesDecode)) {
                    // 4、重新写入到controller
                    this.body = new ByteArrayInputStream(aesDecode.getBytes());
                }
            }
        } catch (Exception e) {
            log.error(SipaConstant.Symbol.BLANK, e);
            throw SystemExceptionFactory.bizException(ESystemErrorCode.NOT_FOUND);
        }
    }

    @Override
    public InputStream getBody() {
        return body;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }
}

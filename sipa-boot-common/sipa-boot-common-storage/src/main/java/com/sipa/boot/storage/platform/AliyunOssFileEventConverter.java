package com.sipa.boot.storage.platform;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipa.boot.core.exception.system.ESystemErrorCode;
import com.sipa.boot.core.exception.system.SystemExceptionFactory;
import com.sipa.boot.storage.AliyunOssEventForm;
import com.sipa.boot.storage.FileEventForm;

import cn.hutool.core.codec.Base64;
import lombok.AllArgsConstructor;

/**
 * <pre>
 * Class Name:AliOssFileEventServiceImpl
 * Package:PACKAGE_NAME
 * Description:
 * </pre>
 *
 * @author guozhipeng
 * @date 2023/6/28 13:47 Version: 1.0
 */
@AllArgsConstructor
public class AliyunOssFileEventConverter implements OssFileEventConverter {
    private final ObjectMapper objectMapper;

    @Override
    public List<FileEventForm> convertEvent(String event) {
        String decodeStr = Base64.decodeStr(event);
        List<AliyunOssEventForm.Event> events;
        try {
            AliyunOssEventForm aliyunOssEventForms = objectMapper.readValue(decodeStr, new TypeReference<>() {});
            events = aliyunOssEventForms.getEvents();
        } catch (JsonProcessingException e) {
            throw SystemExceptionFactory.bizException(ESystemErrorCode.JSON_ERROR, decodeStr);
        }
        return events.stream().map(this::covert).collect(Collectors.toList());
    }

    private FileEventForm covert(AliyunOssEventForm.Event eventForm) {
        FileEventForm fileEventForm = new FileEventForm();

        Instant instant = Instant.parse(eventForm.getEventTime());
        LocalDateTime beijingTime = LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Shanghai"));
        fileEventForm.setEventTime(beijingTime);

        fileEventForm.setEventName(eventForm.getEventName());
        fileEventForm.setSize((long)eventForm.getOss().getObject().getSize());
        fileEventForm.setObjectKey(eventForm.getOss().getObject().getKey());
        fileEventForm.setBucketName(eventForm.getOss().getBucket().getName());
        return fileEventForm;
    }
}

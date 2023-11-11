package com.sipa.boot.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

/**
 * MultipartFile 的包装类
 * 
 * @author caszhou
 * @date 2022/12/23
 */
public class MultipartFileWrapper implements MultipartFile {
    @Setter
    private String name;

    @Setter
    private String originalFileName;

    @Setter
    private String contentType;

    @Setter
    @Getter
    private MultipartFile multipartFile;

    public MultipartFileWrapper(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    @Override
    public String getName() {
        return name != null ? name : multipartFile.getName();
    }

    @Override
    public String getOriginalFilename() {
        return originalFileName != null ? originalFileName : multipartFile.getOriginalFilename();
    }

    @Override
    @Nullable
    public String getContentType() {
        return contentType != null ? contentType : multipartFile.getContentType();
    }

    @Override
    public boolean isEmpty() {
        return multipartFile.isEmpty();
    }

    @Override
    public long getSize() {
        return multipartFile.getSize();
    }

    @Override
    public byte[] getBytes() throws IOException {
        return multipartFile.getBytes();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return multipartFile.getInputStream();
    }

    @Override
    public Resource getResource() {
        return multipartFile.getResource();
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        multipartFile.transferTo(dest);
    }

    @Override
    public void transferTo(Path dest) throws IOException, IllegalStateException {
        multipartFile.transferTo(dest);
    }
}

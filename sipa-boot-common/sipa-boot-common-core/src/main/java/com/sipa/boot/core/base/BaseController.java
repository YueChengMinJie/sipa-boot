package com.sipa.boot.core.base;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import lombok.SneakyThrows;

/**
 * @author caszhou
 * @date 2023/5/30
 */
public abstract class BaseController {
    @SneakyThrows(IOException.class)
    protected static void download(HttpServletResponse response, String fileName, byte[] data) {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition",
            "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));

        byte[] buffer = new byte[1024 * 1024];
        int len;
        OutputStream out = response.getOutputStream();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        while ((len = bais.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        out.flush();
        out.close();
    }
}

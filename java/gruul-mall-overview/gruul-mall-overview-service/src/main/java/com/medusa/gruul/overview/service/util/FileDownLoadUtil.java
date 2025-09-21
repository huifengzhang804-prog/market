package com.medusa.gruul.overview.service.util;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @description:
 * @projectName:gruul-mall-overview
 * @see:com.medusa.gruul.overview.service.util
 * @author:jipeng
 * @createTime:2024/1/6 10:14
 * @version:1.0
 */
@Slf4j
public class FileDownLoadUtil {
    public static Boolean downLoad(String fileName, String filePath, HttpServletResponse response) {
        URL url;
        try {
            url = new URL(filePath);
            URLConnection conn = url.openConnection();
            InputStream inputStream = conn.getInputStream();
            response.reset();
            response.setContentType(conn.getContentType());

            //纯下载方式 文件名应该编码成UTF-8
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));

            byte[] buffer = new byte[1024];
            int len;
            OutputStream outputStream = response.getOutputStream();
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            inputStream.close();
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("文件下载失败", e);
            return Boolean.FALSE;
        }

    }
}

package com.medusa.gruul.common.security.resource.tool;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * 响应处理 工具类
 *
 * @author 张治保
 * date 2022/2/28
 */
@Component
@RequiredArgsConstructor
public class ResponseUtil implements InitializingBean {

    private static List<HttpMessageConverter<?>> messageConverters;
    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    /**
     * 响应数据
     *
     * @param response 响应
     * @throws IOException IO 异常
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void response(HttpServletResponse response, Object value) throws IOException {
        MediaType selectedMediaType = MediaType.APPLICATION_JSON;
        Class<?> valueType = value.getClass();
        //优先尝试使用 HttpMessageConverter
        for (HttpMessageConverter<?> converter : messageConverters) {
            if (converter.canWrite(valueType, selectedMediaType)) {
                ((HttpMessageConverter) converter).write(value, selectedMediaType, new ServletServerHttpResponse(response));
                return;
            }
        }
        //尝试失败 使用默认方式
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().println(JSON.toJSONString(value));
        response.setStatus(HttpServletResponse.SC_OK);
        response.flushBuffer();
    }

    /**
     * 直接响应 跳过 消息转换器
     *
     * @param response response
     * @param value    响应数据
     */
    public static void responseSkipConverters(HttpServletResponse response, Object value) {
        //尝试失败 使用默认方式
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer;
        try {
            writer = response.getWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writer.println(value instanceof String str ? str : JSON.toJSONString(value));
        response.setStatus(HttpServletResponse.SC_OK);
        try {
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void afterPropertiesSet() {
        ResponseUtil.messageConverters = requestMappingHandlerAdapter.getMessageConverters();
    }
}

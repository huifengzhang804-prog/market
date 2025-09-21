package com.medusa.gruul.single;

import com.medusa.gruul.common.system.model.filter.AbstractServletFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 张治保
 * date 2023/9/11
 */
@Slf4j
@Order(Integer.MIN_VALUE)
public class SingleFilter extends AbstractServletFilter {

    private static final String REMOVE_PREFIX_REGEX = "^/(addon-|gruul-)[^/]+";
    private static final Pattern REMOVE_PREFIX_PATTERN = Pattern.compile(REMOVE_PREFIX_REGEX);

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException, ServletException {
        // 获取原始请求URI
        String originalUri = request.getRequestURI();
        // 使用正则表达式移除第一个以"addon-"或"gruul-"开头的path部分
        Matcher matcher = REMOVE_PREFIX_PATTERN.matcher(originalUri);
        if (matcher.find()) {
            String rewriteUri = originalUri.replaceFirst(REMOVE_PREFIX_REGEX, "");
            log.debug("URI已重写：{} -> {}", originalUri, rewriteUri);
            // 继续处理请求链
            filterChain.doFilter(new RequestWrapper(request, rewriteUri), response);
        } else {
            // 如果没有匹配到前缀，则继续处理原始请求
            filterChain.doFilter(request, response);
        }
    }


    public static class RequestWrapper extends HttpServletRequestWrapper {

        private final String uri;

        /**
         * Constructs a request object wrapping the given request.
         *
         * @param request the {@link HttpServletRequest} to be wrapped.
         * @throws IllegalArgumentException if the request is null
         */
        public RequestWrapper(HttpServletRequest request, String uri) {
            super(request);
            this.uri = uri;
        }

        @Override
        public String getRequestURI() {
            return uri;
        }

        /**
         * 兼容單體
         */
        @Override
        public String getServletPath() {
            return uri;
        }
    }
}

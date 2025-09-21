package com.medusa.gruul.common.system.model.filter;

import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 简单包装一下filter
 * ServletRequest 强转 HttpServletRequest
 * ServletResponse 强转 HttpServletResponse
 * @author 张治保
 * date 2021/12/3
 */
public abstract class AbstractServletFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        this.doFilter(request, response, filterChain);
    }

    /**
     * 子类实现
     * @param request request
     * @param response response
     * @param filterChain chain
     * @throws IOException IO流异常
     * @throws ServletException servlet异常
     */
    public abstract void doFilter( HttpServletRequest request,HttpServletResponse response ,FilterChain filterChain) throws IOException, ServletException;
}

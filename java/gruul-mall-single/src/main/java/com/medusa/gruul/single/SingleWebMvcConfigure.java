package com.medusa.gruul.single;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.tool.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * @author 张治保
 * date 2023/9/6
 */
@Configuration
@RequiredArgsConstructor
public class SingleWebMvcConfigure implements WebMvcConfigurer {

    private final SingleProperties singleProperties;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SingleInterceptor(singleProperties))
                .addPathPatterns("/system/addon/addons");
    }

    @RequiredArgsConstructor
    public static class SingleInterceptor implements HandlerInterceptor {

        private final SingleProperties singleProperties;

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            ResponseUtil.response(
                    response,
                    Result.ok(CollUtil.emptyIfNull(singleProperties.getAddons()))
            );
            return false;
        }
    }
}

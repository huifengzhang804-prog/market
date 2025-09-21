package com.medusa.gruul.common.security.resource;

import cn.hutool.core.map.WeakConcurrentMap;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import io.vavr.control.Option;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 使用 uri 作为权限校验方式 优先级比 {@link PreAuthorize} 低
 *
 * @author 张治保
 * @since 2023/11/4
 */
@Slf4j
@RequiredArgsConstructor
public final class AccessAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private static final Map<HandlerMethod, Boolean> HANDLER_METHOD_CACHE = new WeakConcurrentMap<>();
    private static final Set<Class<? extends Annotation>> AUTH_ANNOTATION_CLASSES = Set.of(
            PreAuthorize.class,
            PermitAll.class,
            DenyAll.class
    );
    private final HandlerMapping handlerMapping;

    /**
     * 获取 去除?及后面的参数的 perm
     *
     * @param perm 权限
     * @return 去除
     */
    private String removeParam(String perm) {
        int index = perm.indexOf('?');
        if (index > 0) {
            perm = perm.substring(0, index);
        }
        return perm;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext requestContext) {
        HttpServletRequest request = requestContext.getRequest();
        //如果 handler 包含 PreAuthorize 注解则直接放行
        if (annotatedByPreAuthorize(request)) {
            return new AuthorizationDecision(true);
        }
        Option<SecureUser<Object>> userOpt = ISecurity.userOpt();
        if (userOpt.isEmpty()) {
            return new AuthorizationDecision(false);
        }
        SecureUser<Object> secureUser = userOpt.get();
        boolean granted = secureUser.getRoles().contains(Roles.SUPER_ADMIN);
        if (!granted) {
            granted = secureUser.getPerms().stream()
                    .map(perm -> new AntPathRequestMatcher(removeParam(perm)))
                    .anyMatch(matcher -> matcher.matches(request));
        }
        if (!granted) {
            log.debug("用户id：{} 没有权限访问:{}", secureUser.getId(), request.getRequestURI());
        }
        return new AuthorizationDecision(granted);
    }

    /**
     * 判断 handler 是否包含 PreAuthorize 注解
     *
     * @param request 请求
     * @return true 包含
     */
    private boolean annotatedByPreAuthorize(HttpServletRequest request) {
        HandlerExecutionChain handler;
        try {
            handler = handlerMapping.getHandler(request);
        } catch (Exception e) {
            log.debug("获取 HandlerExecutionChain 失败", e);
            return false;
        }
        if (handler == null) {
            return false;
        }
        Object targetHandler = handler.getHandler();
        if (!(targetHandler instanceof HandlerMethod handlerMethod)) {
            return false;
        }
        return findPreAuthorizeAnnotation(handlerMethod);
    }

    /**
     * 查找 PreAuthorize 注解
     *
     * @param handlerMethod 方法
     * @return PreAuthorize
     */
    private boolean findPreAuthorizeAnnotation(HandlerMethod handlerMethod) {
        return HANDLER_METHOD_CACHE
                .computeIfAbsent(
                        handlerMethod,
                        (method) -> {
                            for (Class<? extends Annotation> authAnnotationClass : AUTH_ANNOTATION_CLASSES) {
                                if (handlerMethod.hasMethodAnnotation(authAnnotationClass) || AnnotatedElementUtils.isAnnotated(handlerMethod.getBeanType(), authAnnotationClass)) {
                                    return true;
                                }
                            }
                            return false;
                        }
                );

    }
}

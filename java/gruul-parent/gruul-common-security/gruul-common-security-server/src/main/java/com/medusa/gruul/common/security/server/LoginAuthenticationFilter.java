package com.medusa.gruul.common.security.server;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.resource.exception.SecurityException;
import com.medusa.gruul.common.security.server.annotation.GrantType;
import com.medusa.gruul.common.security.server.handler.FailureHandler;
import com.medusa.gruul.common.security.server.handler.SuccessHandler;
import com.medusa.gruul.common.security.server.model.AuthenticationRequest;
import com.medusa.gruul.common.security.server.provider.IAuthenticationProvider;
import com.medusa.gruul.common.security.server.tool.JwtEncoder;
import com.medusa.gruul.global.model.exception.Error;
import com.medusa.gruul.global.model.validate.ValidFieldError;
import com.medusa.gruul.global.model.validate.ValidateHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.function.ServerRequest;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录认证过滤器 用于处理登录逻辑 参考 {@link org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter}
 *
 * @author 张治保
 * @since 2023/11/2
 */
public final class LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/login", "POST");
    private final Map<String, Class<? extends AuthenticationRequest>> requestMapping = new ConcurrentHashMap<>();
    private final List<HttpMessageConverter<?>> messageConverters;

    public LoginAuthenticationFilter(boolean repeatLogin, List<IAuthenticationProvider<? extends AuthenticationRequest>> providers, JwtEncoder jwtEncoder, List<HttpMessageConverter<?>> messageConverters) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
        this.messageConverters = messageConverters;
        super.setAuthenticationManager(new ProviderManager(providers.stream().map(provider -> (AuthenticationProvider) provider).toList()));
        super.setAuthenticationSuccessHandler(new SuccessHandler(repeatLogin, jwtEncoder));
        super.setAuthenticationFailureHandler(new FailureHandler());
        this.renderRequestMapping(providers);
    }

    private void renderRequestMapping(List<IAuthenticationProvider<? extends AuthenticationRequest>> providers) {
        providers.forEach(
                provider -> {
                    GrantType annotation = AnnotationUtils.findAnnotation(provider.getClass(), GrantType.class);
                    if (annotation == null) {
                        throw new RuntimeException("authentication provider must be annotated by @GrantType");
                    }
                    requestMapping.put(annotation.value(), provider.requestType());
                }
        );
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //获取 grantType
        String grantTypeStr = request.getParameter(GrantType.PARAMETER_NAME);
        //参数转换
        AuthenticationRequest autRequest;
        try {
            autRequest = ServerRequest.create(request, this.messageConverters).body(this.requestType(grantTypeStr));
        } catch (ServletException | IOException ex) {
            logger.error("request body read error", ex);
            autRequest = null;
        }
        if (autRequest == null) {
            throw SecurityException.of(SecureCodes.REQUEST_INVALID);
        }
        //参数校验
        Set<ConstraintViolation<AuthenticationRequest>> violations = ValidateHelper.validate(autRequest);
        if (CollUtil.isNotEmpty(violations)) {
            throw SecurityException.of(SecureCodes.DATA_VALID_ERROR.dataEx(ValidFieldError.of(violations)));
        }
        //交由 AuthenticationManager 处理
        try {
            return this.getAuthenticationManager().authenticate(autRequest);
        } catch (Exception ex) {
            if (ex instanceof Error error) {
                throw SecurityException.of(error);
            }
            throw ex;
        }
    }

    /**
     * 根据 grantType 获取对应的 AuthenticationRequest类型
     *
     * @param grantType grantType
     * @return AuthenticationRequest 类型
     */
    private Class<? extends AuthenticationRequest> requestType(String grantType) {
        grantType = StrUtil.trimToNull(grantType);
        if (StrUtil.isEmpty(grantType)) {
            throw SecurityException.of(SecureCodes.REQUEST_INVALID.exception());
        }
        Class<? extends AuthenticationRequest> requestType = requestMapping.get(grantType);
        if (requestType == null) {
            throw SecurityException.of(SecureCodes.GRANT_INVALID.dataEx(grantType));
        }
        return requestType;
    }


}

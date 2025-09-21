# io.jsonwebtoken.impl.CustomJwtParser

**文件路径**: `jsonwebtoken\impl\CustomJwtParser.java`

## 代码文档
///
@author 张治保
@since 2023/11/29
 
///


## 文件结构
```
项目根目录
└── jsonwebtoken\impl
    └── CustomJwtParser.java
```

## 完整代码
```java
package io.jsonwebtoken.impl;

import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.JSONReader;
import com.medusa.gruul.common.security.resource.tool.jwt.JwtDeserializer;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.io.DelegateStringDecoder;
import io.jsonwebtoken.impl.security.ConstantKeyLocator;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.lang.Objects;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.security.PublicKey;
import java.util.Map;
import java.util.Set;

/**
 * @author 张治保
 * @since 2023/11/29
 */
public final class CustomJwtParser extends DefaultJwtParser {

    @SuppressWarnings("deprecation")
    public CustomJwtParser(PublicKey signPublicKey) {
        super(
                null,
                null, false,
                false,
                new ConstantKeyLocator(signPublicKey, null),
                DefaultClock.INSTANCE,
                Set.of(),
                0,
                (DefaultClaims) Jwts.claims().build(),
                new DelegateStringDecoder(Decoders.BASE64URL),
                new JwtDeserializer<>(),
                null,
                Jwts.ZIP.get(),
                Jwts.SIG.get(),
                Jwts.KEY.get(),
                Jwts.ENC.get()
        );
    }

    @SneakyThrows
    @Override
    protected Map<String, ?> deserialize(InputStream in, String name) {
        try {
            return JSONB.parseObject(in, Map.class, JSONReader.Feature.SupportArrayToBean, JSONReader.Feature.Base64StringAsByteArray);
        } finally {
            Objects.nullSafeClose(in);
        }
    }
}

```

# com.medusa.gruul.common.security.resource.AccessAuthorizationManager

**文件路径**: `security\resource\AccessAuthorizationManager.java`

## 代码文档
///
使用 uri 作为权限校验方式 优先级比 {@link PreAuthorize} 低

@author 张治保
@since 2023/11/4
 
///

///
获取 去除?及后面的参数的 perm

@param perm 权限
@return 去除
     
///

///
判断 handler 是否包含 PreAuthorize 注解

@param request 请求
@return true 包含
     
///

///
查找 PreAuthorize 注解

@param handlerMethod 方法
@return PreAuthorize
     
///


## 文件结构
```
项目根目录
└── security\resource
    └── AccessAuthorizationManager.java
```

## 完整代码
```java
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

```

# com.medusa.gruul.common.security.resource.SecurityClientAutoconfigure

**文件路径**: `security\resource\SecurityClientAutoconfigure.java`

## 代码文档
///
b

@author 张治保
date 2022/2/25
 
///

///
默认的密码转换器
algorithm

@return BCryptPasswordEncoder
     
///

///
jwt秘钥 用于加解密

@param clientProperties 客户端配置
@return IJwtSecretKey
     
///


## 文件结构
```
项目根目录
└── security\resource
    └── SecurityClientAutoconfigure.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.resource;

import com.medusa.gruul.common.security.resource.config.ClientAndTokenStateConfig;
import com.medusa.gruul.common.security.resource.config.SecurityClientConfig;
import com.medusa.gruul.common.security.resource.exception.SecureExceptionAdvice;
import com.medusa.gruul.common.security.resource.helper.Security;
import com.medusa.gruul.common.security.resource.properties.SecurityClientProperties;
import com.medusa.gruul.common.security.resource.tool.ResponseUtil;
import com.medusa.gruul.common.security.resource.tool.jwt.ClientJwtSecretKey;
import com.medusa.gruul.common.security.resource.tool.jwt.JwtDecoder;
import com.medusa.gruul.common.security.resource.tool.jwt.JwtSecurityContextRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * b
 *
 * @author 张治保
 * date 2022/2/25
 */
@Import({
        SecureExceptionAdvice.class,
        Security.class,
        ClientAndTokenStateConfig.class,
        SecurityClientConfig.class,
        JwtDecoder.class,
        JwtSecurityContextRepository.class,
        ResponseUtil.class
})
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
@EnableConfigurationProperties(SecurityClientProperties.class)
public class SecurityClientAutoconfigure {

    /**
     * 默认的密码转换器
     * algorithm
     *
     * @return BCryptPasswordEncoder
     */
    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * jwt秘钥 用于加解密
     *
     * @param clientProperties 客户端配置
     * @return IJwtSecretKey
     */
    @Bean
    public ClientJwtSecretKey clientJwtSecretKey(SecurityClientProperties clientProperties) {
        return new ClientJwtSecretKey(clientProperties.getPublicKey(), clientProperties.getPayloadKey());
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new UnsupportedOperationException();
        };
    }
}

```

# com.medusa.gruul.common.security.resource.config.ClientAndTokenStateConfig

**文件路径**: `resource\config\ClientAndTokenStateConfig.java`

## 代码文档
///
有状态token

@author 张治保
 
///

///
")
                .excludePathPatterns("/error");
    }

    public final static class TokenStateHandlerInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
            //用户的登录信息
            SecureUser<?> user = ISecurity.secureUser();
            if (user.isAnonymous()) {
                return true;
            }
            //请求头店铺id
            Systems systems = SystemContextHolder.get();
            this.checkSystems(systems);
            //用户登录信息的店铺id
            Long shopId = user.getShopId();
            //检查客户端状态
            ClientType clientType = systems.getClientType();
            checkClientData(clientType, systems.getShopId(), shopId);
            //检查用户登录状态
            return true;
        }

        private void checkSystems(Systems systems) {
            if (systems == null || systems.getShopId() == null || systems.getClientType() == null) {
                throw SecureCodes.REQUEST_INVALID.exception();
            }
        }

        /**
检查客户端数据
         
///


## 文件结构
```
项目根目录
└── resource\config
    └── ClientAndTokenStateConfig.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.resource.config;

import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.context.SystemContextHolder;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.common.system.model.model.Systems;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * 有状态token
 *
 * @author 张治保
 */
@RequiredArgsConstructor
public final class ClientAndTokenStateConfig implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TokenStateHandlerInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/error");
    }

    public final static class TokenStateHandlerInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
            //用户的登录信息
            SecureUser<?> user = ISecurity.secureUser();
            if (user.isAnonymous()) {
                return true;
            }
            //请求头店铺id
            Systems systems = SystemContextHolder.get();
            this.checkSystems(systems);
            //用户登录信息的店铺id
            Long shopId = user.getShopId();
            //检查客户端状态
            ClientType clientType = systems.getClientType();
            checkClientData(clientType, systems.getShopId(), shopId);
            //检查用户登录状态
            return true;
        }

        private void checkSystems(Systems systems) {
            if (systems == null || systems.getShopId() == null || systems.getClientType() == null) {
                throw SecureCodes.REQUEST_INVALID.exception();
            }
        }

        /**
         * 检查客户端数据
         */
        private void checkClientData(ClientType clientType, Long headerShopId, Long userShopId) {
            if (clientType.getShopIdCheck().apply(headerShopId, userShopId)) {
                return;
            }
            throw SecureCodes.REQUEST_INVALID.exception();
        }
    }
}
```

# com.medusa.gruul.common.security.resource.config.SecurityClientConfig

**文件路径**: `resource\config\SecurityClientConfig.java`

## 代码文档
///
@author 张治保
date 2022/2/28
 
///


## 文件结构
```
项目根目录
└── resource\config
    └── SecurityClientConfig.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.resource.config;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.security.resource.AccessAuthorizationManager;
import com.medusa.gruul.common.security.resource.exception.ExceptionEntryPoint;
import com.medusa.gruul.common.security.resource.properties.SecurityClientProperties;
import com.medusa.gruul.common.security.resource.tool.SecurityChainConfig;
import com.medusa.gruul.common.security.resource.tool.jwt.JwtSecurityContextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;

/**
 * @author 张治保
 * date 2022/2/28
 */
@Configuration
@RequiredArgsConstructor
public class SecurityClientConfig {

    private final SecurityClientProperties resourceProperties;
    private final JwtSecurityContextRepository securityContextRepository;
    private final HandlerMapping requestMappingHandlerMapping;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, @Nullable List<SecurityChainConfig> securityChainConfigs) throws Exception {
        //白名单
        SecurityClientProperties.UriPermission uriPermission = resourceProperties.getUriPermission();
        //定义请求授权规则
        http.authorizeHttpRequests(
                request -> {
                    if (uriPermission.isEnable()) {
                        request.requestMatchers(uriPermission.getPermits()).permitAll()
                                .anyRequest().access(new AccessAuthorizationManager(requestMappingHandlerMapping));
                        return;
                    }
                    request.anyRequest().permitAll();
                }
        );
        //enable Frame Options
        if (!resourceProperties.isEnableFrameOptions()) {
            http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        }
        //定义登录登出信息
        //禁用表单登录
        ExceptionEntryPoint authenticationEntryPoint = new ExceptionEntryPoint();
        http.formLogin(AbstractHttpConfigurer::disable)
                //禁用跨域配置
                .cors(AbstractHttpConfigurer::disable)
                //禁用csrf
                .csrf(AbstractHttpConfigurer::disable)
                //
                //无状态 session
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //定义上认证信息下文处理逻辑
                .securityContext(context -> context.securityContextRepository(securityContextRepository))
                //定义异常处理
                .exceptionHandling(
                        ex -> ex.authenticationEntryPoint(authenticationEntryPoint)
                                .accessDeniedHandler(authenticationEntryPoint)
                )
                //ExceptionTranslationFilter 提升到SecurityContextHolderFilter前面
                .addFilterBefore(new ExceptionTranslationFilter(authenticationEntryPoint, new NullRequestCache()), SecurityContextHolderFilter.class);
        //自定义部分
        CollUtil.emptyIfNull(securityChainConfigs).forEach(config -> config.config(http));
        return http.build();
    }


}

```

# com.medusa.gruul.common.security.resource.exception.ExceptionEntryPoint

**文件路径**: `resource\exception\ExceptionEntryPoint.java`

## 代码文档
///
认证异常处理

@author 张治保
 
///


## 文件结构
```
项目根目录
└── resource\exception
    └── ExceptionEntryPoint.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.resource.exception;

import com.medusa.gruul.common.security.resource.tool.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/**
 * 认证异常处理
 *
 * @author 张治保
 */
@Slf4j
public final class ExceptionEntryPoint implements AccessDeniedHandler, AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) throws IOException {
        this.handleError(response, ex);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        this.handleError(response, accessDeniedException);
    }

    @SneakyThrows
    private void handleError(HttpServletResponse response, Throwable throwable) {
        ResponseUtil.response(response, SecureExceptionAdvice.authExceptionMapping(throwable));

    }
}

```

# com.medusa.gruul.common.security.resource.exception.SecureExceptionAdvice

**文件路径**: `resource\exception\SecureExceptionAdvice.java`

## 代码文档
///
异常处理器

@author 张治保
date 2022/3/1
 
///

///
认证异常映射

@param ex 异常
@return Result
     
///

///
认证异常处理

@param ex 异常
@return Result
     
///

///
判断ex 是否在classes实现类型中
     
///

///
AuthenticationException 认证服务异常
     
///


## 文件结构
```
项目根目录
└── resource\exception
    └── SecureExceptionAdvice.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.resource.exception;

import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常处理器
 *
 * @author 张治保
 * date 2022/3/1
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class SecureExceptionAdvice {

    /**
     * 认证异常映射
     *
     * @param ex 异常
     * @return Result
     */
    private static Result<Void> authExMapping(AuthenticationException ex) {
        if (ex.getCause() instanceof Error error) {
            return Result.failed(error);
        }
        if (isMachInstance(ex, InsufficientAuthenticationException.class)) {
            return Result.failed(SecureCodes.NEED_LOGIN);
        }
        if (isMachInstance(
                ex,
                UsernameNotFoundException.class,
                AuthenticationCredentialsNotFoundException.class,
                BadCredentialsException.class,
                CredentialsExpiredException.class,
                PreAuthenticatedCredentialsNotFoundException.class
        )
        ) {
            return Result.failed(SecureCodes.USERNAME_PASSWORD_INVALID);
        }

        if (isMachInstance(
                ex,
                AuthenticationServiceException.class,
                InternalAuthenticationServiceException.class,
                ProviderNotFoundException.class
        )) {
            return Result.failed(SecureCodes.AUTH_SERVER_ERROR);
        }
        if (isMachInstance(
                ex,
                AccountExpiredException.class,
                DisabledException.class,
                LockedException.class,
                NonceExpiredException.class)) {
            return Result.failed(SecureCodes.ACCOUNT_INVALID);
        }

        if (isMachInstance(
                ex,
                RememberMeAuthenticationException.class,
                CookieTheftException.class,
                InvalidCookieException.class,
                SessionAuthenticationException.class

        )) {
            return Result.failed(SecureCodes.ACCESS_DENIED);
        }
        return Result.failed(ex.getMessage());
    }

    /**
     * 认证异常处理
     *
     * @param ex 异常
     * @return Result
     */

    public static Result<Void> authExceptionMapping(Throwable ex) {
        log.debug("authExceptionMapping", ex);
        if (ex instanceof AuthenticationException authenticationException) {
            return authExMapping(authenticationException);
        }
        if (ex.getCause() instanceof AuthenticationException authenticationException) {
            return authExMapping(authenticationException);
        }
        if (ex instanceof AccessDeniedException && ISecurity.isAnonymous()) {
            return Result.failed(SecureCodes.NEED_LOGIN);
        }
        return Result.failed(SecureCodes.PERMISSION_DENIED);
    }

    /**
     * 判断ex 是否在classes实现类型中
     */
    public static boolean isMachInstance(Object ex, Class<?>... classes) {
        for (Class<?> clazz : classes) {
            if (clazz.isAssignableFrom(ex.getClass())) {
                return true;
            }
        }
        return false;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> accessDeniedException(AccessDeniedException ex) {
        return SecureExceptionAdvice.authExceptionMapping(ex);
    }

    /**
     * AuthenticationException 认证服务异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public Result<Void> authException(AuthenticationException ex) {
        return SecureExceptionAdvice.authExceptionMapping(ex);
    }

}

```

# com.medusa.gruul.common.security.resource.exception.SecurityException

**文件路径**: `resource\exception\SecurityException.java`

## 代码文档
///
自行封装的安全异常

@author 张治保
@since 2023/11/3
 
///


## 文件结构
```
项目根目录
└── resource\exception
    └── SecurityException.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.resource.exception;

import com.medusa.gruul.global.model.exception.Error;
import com.medusa.gruul.global.model.exception.GlobalException;
import org.springframework.security.core.AuthenticationException;

/**
 * 自行封装的安全异常
 *
 * @author 张治保
 * @since 2023/11/3
 */
public class SecurityException extends AuthenticationException {

    private SecurityException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public static AuthenticationException of(Error error) {
        if (error instanceof GlobalException globalException) {
            return of(globalException);
        }
        return new SecurityException(error.exception());
    }

    public static AuthenticationException of(GlobalException exception) {
        return new SecurityException(exception);
    }
}

```

# com.medusa.gruul.common.security.resource.extension.IRolePermMatcher

**文件路径**: `resource\extension\IRolePermMatcher.java`

## 代码文档
///
@author 张治保
date 2023/4/13
 
///

///
开启允许匿名访问

@return IRolePermMatcher 条件组装器
     
///

///
不包含角色匹配

@param role 角色
@return IRolePermMatcher 条件组装器
     
///

///
不包含权限匹配

@param perm 权限
@return IRolePermMatcher 条件组装器
     
///

///
不包含子角色匹配

@param role 子角色
@return IRolePermMatcher 条件组装器
     
///

///
不包含资源（权限/角色）匹配

@param condition 权限条件
@param property  权限/角色列
@return IRolePermMatcher 条件组装器
     
///

///
匹配该角色

@param condition 角色
@return IRolePermMatcher 条件组装器
     
///

///
匹配该权限

@param perm 权限
@return IRolePermMatcher 条件组装器
     
///

///
匹配该子角色

@param condition 子角色
@return IRolePermMatcher 条件组装器
     
///

///
资源匹配（权限/角色）

@param condition 权限条件
@param property  权限/角色列
@return IRolePermMatcher 条件组装器
     
///

///
任意角色匹配

@param roles 角色
@return IRolePermMatcher 条件组装器
     
///

///
任意权限匹配

@param perms 权限
@return IRolePermMatcher 条件组装器
     
///

///
任意子角色匹配

@param roles 子角色
@return IRolePermMatcher 条件组装器
     
///

///
是否包含任意权限/角色

@param conditions 权限条件
@param property   权限/角色列
@return IRolePermMatcher 条件组装器
     
///

///
匹配所有角色

@param roles 角色
@return IRolePermMatcher 条件组装器
     
///

///
匹配所有权限

@param perms 权限
@return IRolePermMatcher 条件组装器
     
///

///
匹配所有子角色

@param condition 子角色
@return IRolePermMatcher 条件组装器
     
///

///
匹配所有权限/角色

@param conditions 条件权限
@param property   权限/角色列
@return IRolePermMatcher 条件组装器
     
///

///
and 条件

@param and 条件组装器
@return IRolePermMatcher 条件组装器
     
///

///
or 条件

@param or 条件组装器
@return IRolePermMatcher 条件组装器
     
///

///
自定义条件匹配

@param custom 自定义条件
              true 匹配成功
              false 匹配失败
              null  不参与匹配
@return IRolePermMatcher 条件组装器
     
///


## 文件结构
```
项目根目录
└── resource\extension
    └── IRolePermMatcher.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.resource.extension;

import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.Roles;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author 张治保
 * date 2023/4/13
 */
public interface IRolePermMatcher<S extends IRolePermMatcher<S>> {

    /**
     * 开启允许匿名访问
     *
     * @return IRolePermMatcher 条件组装器
     */
    S anonymous();

    /**
     * 不包含角色匹配
     *
     * @param role 角色
     * @return IRolePermMatcher 条件组装器
     */
    default S neqRole(Roles role) {
        return neq(SecureUser::getRoles, role);
    }

    /**
     * 不包含权限匹配
     *
     * @param perm 权限
     * @return IRolePermMatcher 条件组装器
     */
    default S neqPerm(String perm) {
        return neq(SecureUser::getPerms, perm);
    }

    /**
     * 不包含子角色匹配
     *
     * @param role 子角色
     * @return IRolePermMatcher 条件组装器
     */
    default S neqSubRole(Roles role) {
        return neq(SecureUser::getSubRoles, role);
    }

    /**
     * 不包含资源（权限/角色）匹配
     *
     * @param condition 权限条件
     * @param property  权限/角色列
     * @return IRolePermMatcher 条件组装器
     */
    <T> S neq(Function<SecureUser<?>, Set<T>> property, T condition);


    /**
     * 匹配该角色
     *
     * @param condition 角色
     * @return IRolePermMatcher 条件组装器
     */
    default S role(Roles condition) {
        return eq(SecureUser::getRoles, condition);
    }

    /**
     * 匹配该权限
     *
     * @param perm 权限
     * @return IRolePermMatcher 条件组装器
     */
    default S perm(String perm) {
        return eq(SecureUser::getPerms, perm);
    }

    /**
     * 匹配该子角色
     *
     * @param condition 子角色
     * @return IRolePermMatcher 条件组装器
     */
    default S subRole(Roles condition) {
        return eq(SecureUser::getSubRoles, condition);
    }

    /**
     * 资源匹配（权限/角色）
     *
     * @param condition 权限条件
     * @param property  权限/角色列
     * @return IRolePermMatcher 条件组装器
     */
    <T> S eq(Function<SecureUser<?>, Set<T>> property, T condition);

    /**
     * 任意角色匹配
     *
     * @param roles 角色
     * @return IRolePermMatcher 条件组装器
     */
    default S anyRole(Roles... roles) {
        return any(SecureUser::getRoles, roles);
    }

    /**
     * 任意权限匹配
     *
     * @param perms 权限
     * @return IRolePermMatcher 条件组装器
     */
    default S anyPerm(String... perms) {
        return any(SecureUser::getPerms, perms);
    }

    /**
     * 任意子角色匹配
     *
     * @param roles 子角色
     * @return IRolePermMatcher 条件组装器
     */
    default S anySubRole(Roles... roles) {
        return any(SecureUser::getSubRoles, roles);
    }

    /**
     * 是否包含任意权限/角色
     *
     * @param conditions 权限条件
     * @param property   权限/角色列
     * @return IRolePermMatcher 条件组装器
     */
    @SuppressWarnings("unchecked")
    <T> S any(Function<SecureUser<?>, Set<T>> property, T... conditions);


    /**
     * 匹配所有角色
     *
     * @param roles 角色
     * @return IRolePermMatcher 条件组装器
     */
    default S roles(Roles... roles) {
        return all(SecureUser::getRoles, roles);
    }

    /**
     * 匹配所有权限
     *
     * @param perms 权限
     * @return IRolePermMatcher 条件组装器
     */
    default S perms(String... perms) {
        return all(SecureUser::getPerms, perms);
    }

    /**
     * 匹配所有子角色
     *
     * @param condition 子角色
     * @return IRolePermMatcher 条件组装器
     */
    default S subRoles(Roles... condition) {
        return all(SecureUser::getSubRoles, condition);
    }

    /**
     * 匹配所有权限/角色
     *
     * @param conditions 条件权限
     * @param property   权限/角色列
     * @return IRolePermMatcher 条件组装器
     */
    @SuppressWarnings("unchecked")
    <T> S all(Function<SecureUser<?>, Set<T>> property, T... conditions);


    /**
     * and 条件
     *
     * @param and 条件组装器
     * @return IRolePermMatcher 条件组装器
     */
    S and(Consumer<IRolePermMatcher<?>> and);

    /**
     * or 条件
     *
     * @param or 条件组装器
     * @return IRolePermMatcher 条件组装器
     */
    S or(Consumer<IRolePermMatcher<?>> or);

    /**
     * 自定义条件匹配
     *
     * @param custom 自定义条件
     *               true 匹配成功
     *               false 匹配失败
     *               null  不参与匹配
     * @return IRolePermMatcher 条件组装器
     */
    S custom(Function<IRolePermMatcher<?>, Boolean> custom);
}

```

# com.medusa.gruul.common.security.resource.extension.IUserRolePermMatcher

**文件路径**: `resource\extension\IUserRolePermMatcher.java`

## 代码文档
///
用户角色匹配器

@author 张治保
date 2023/4/11
 
///

///
获取用户 信息

@return SecureUser 用户信息
     
///

///
计算结果 执行匹配

@return 是否匹配成功
     
///

///
获取用户与执行结果

@return UserMatch 用户与执行结果信息
     
///


## 文件结构
```
项目根目录
└── resource\extension
    └── IUserRolePermMatcher.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.resource.extension;

import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.bean.UserMatch;

/**
 * 用户角色匹配器
 *
 * @author 张治保
 * date 2023/4/11
 */
public interface IUserRolePermMatcher extends IRolePermMatcher<IUserRolePermMatcher> {
    /**
     * 获取用户 信息
     *
     * @return SecureUser 用户信息
     */
    <T> SecureUser<T> getUser();

    /**
     * 计算结果 执行匹配
     *
     * @return 是否匹配成功
     */
    boolean match();


    /**
     * 获取用户与执行结果
     *
     * @return UserMatch 用户与执行结果信息
     */
    UserMatch userMatch();

}

```

# com.medusa.gruul.common.security.resource.extension.RolePermConsumer

**文件路径**: `resource\extension\RolePermConsumer.java`

## 代码文档
///
角色权限消费器 用于 and、 or、 customer

@author 张治保
date 2023/4/12
 
///

///
任务队列
     
///


## 文件结构
```
项目根目录
└── resource\extension
    └── RolePermConsumer.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.resource.extension;

import com.medusa.gruul.common.security.model.bean.SecureUser;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 角色权限消费器 用于 and、 or、 customer
 *
 * @author 张治保
 * date 2023/4/12
 */
public final class RolePermConsumer implements IRolePermMatcher<RolePermConsumer>, Consumer<IRolePermMatcher<?>> {

    /**
     * 任务队列
     */
    private final Queue<Consumer<IRolePermMatcher<?>>> taskQueue = new LinkedList<>();

    @Override
    public void accept(IRolePermMatcher matcher) {
        while (!taskQueue.isEmpty()) {
            taskQueue.poll().accept(matcher);
        }
    }

    @Override
    public RolePermConsumer anonymous() {
        taskQueue.add(IRolePermMatcher::anonymous);
        return this;
    }

    @Override
    public <T> RolePermConsumer neq(Function<SecureUser<?>, Set<T>> property, T condition) {
        taskQueue.add(matcher -> matcher.neq(property, condition));
        return this;
    }

    @Override
    public <T> RolePermConsumer eq(Function<SecureUser<?>, Set<T>> property, T condition) {
        taskQueue.add(matcher -> matcher.eq(property, condition));
        return this;
    }

    @Override
    @SafeVarargs
    public final <T> RolePermConsumer any(Function<SecureUser<?>, Set<T>> property, T... conditions) {
        taskQueue.add(matcher -> matcher.any(property, conditions));
        return this;
    }

    @Override
    @SafeVarargs
    public final <T> RolePermConsumer all(Function<SecureUser<?>, Set<T>> property, T... conditions) {
        taskQueue.add(matcher -> matcher.all(property, conditions));
        return this;
    }

    @Override
    public RolePermConsumer and(Consumer<IRolePermMatcher<?>> and) {
        taskQueue.add(matcher -> matcher.and(and));
        return this;
    }

    @Override
    public RolePermConsumer or(Consumer<IRolePermMatcher<?>> or) {
        taskQueue.add(matcher -> matcher.or(or));
        return this;
    }

    @Override
    public RolePermConsumer custom(Function<IRolePermMatcher<?>, Boolean> custom) {
        taskQueue.add(matcher -> matcher.custom(custom));
        return this;
    }
}

```

# com.medusa.gruul.common.security.resource.extension.RolePermMatcher

**文件路径**: `resource\extension\RolePermMatcher.java`

## 代码文档
///
用户 角色权限 匹配器

@author 张治保
date 2023/4/10
 
///

///
当前用户
     
///

///
匹配结果
     
///

///
or 条件初始化兼容
     
///

///
构造器

@param authentication 认证信息
     
///

///
构造器

@param user 用户信息
@param <T>  用户额外信息类型
     
///

///
获取用户信息

@param authentication 用户信息
@return {@link SecureUser} 用户信息
     
///

///
条件组装器 匿名用户

@return {@link RolePermMatcher} 条件组装器
     
///

///
条件组装器 且  必须不包含

@param property  权限/角色列
@param condition 权限条件
@param <T>       权限/角色类型
@return {@link IUserRolePermMatcher} 条件组装器
     
///

///
条件组装器 且  必须包含匹配

@param property  权限/角色列
@param condition 权限条件
@param <T>       权限/角色类型
@return {@link IUserRolePermMatcher} 条件组装器
     
///

///
条件组装器 且  任一条件包含匹配

@param property   权限/角色列
@param conditions 权限条件
@param <T>        权限/角色类型
@return {@link IUserRolePermMatcher} 条件组装器
     
///

///
条件组装器 且  所有条件都包含匹配

@param property   权限/角色列
@param conditions 条件权限
@param <T>        权限/角色类型
@return {@link IUserRolePermMatcher} 条件组装器
     
///

///
条件组装器 且

@param and 条件组装器
@return {@link IUserRolePermMatcher} 条件组装器
     
///

///
条件组装器 或

@param or 条件组装器
@return {@link IUserRolePermMatcher} 条件组装器
     
///

///
非黑名单用户 且 匹配成功

@return boolean 是否匹配成功 true:匹配成功 false:匹配失败
     
///

///
获取用户匹配结果 与用户信息

@return {@link UserMatch} 用户匹配结果 与用户信息
     
///


## 文件结构
```
项目根目录
└── resource\extension
    └── RolePermMatcher.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.resource.extension;

import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.bean.UserMatch;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.security.resource.model.SecureUserAuthentication;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 用户 角色权限 匹配器
 *
 * @author 张治保
 * date 2023/4/10
 */
public final class RolePermMatcher implements IUserRolePermMatcher {

    /**
     * 当前用户
     */
    private final SecureUser<?> user;

    /**
     * 匹配结果
     */
    private boolean success = true;

    /**
     * or 条件初始化兼容
     */
    private boolean orFirst = true;

    /**
     * 构造器
     *
     * @param authentication 认证信息
     */
    public RolePermMatcher(Authentication authentication) {
        // 如果上下文中没有用户信息，则从authentication中获取
        this(userFromAuthentication(authentication));
    }

    /**
     * 构造器
     *
     * @param user 用户信息
     * @param <T>  用户额外信息类型
     */
    public <T> RolePermMatcher(SecureUser<T> user) {
        this.user = user;
    }

    /**
     * 获取用户信息
     *
     * @param authentication 用户信息
     * @return {@link SecureUser} 用户信息
     */
    private static SecureUser<?> userFromAuthentication(Authentication authentication) {
        if (!(authentication instanceof SecureUserAuthentication secureUserAuthentication) || ISecurity.isAnonymous(authentication)) {
            return new SecureUser<>();
        }
        return secureUserAuthentication.user();
    }

    /**
     * 条件组装器 匿名用户
     *
     * @return {@link RolePermMatcher} 条件组装器
     */
    @Override
    public IUserRolePermMatcher anonymous() {
        if (success) {
            this.update(user.isAnonymous());
        }
        return this;
    }


    /**
     * 条件组装器 且  必须不包含
     *
     * @param property  权限/角色列
     * @param condition 权限条件
     * @param <T>       权限/角色类型
     * @return {@link IUserRolePermMatcher} 条件组装器
     */
    @Override
    public <T> IUserRolePermMatcher neq(Function<SecureUser<?>, Set<T>> property, T condition) {
        if (success) {
            this.update(!property.apply(user).contains(condition));
        }
        return this;
    }

    /**
     * 条件组装器 且  必须包含匹配
     *
     * @param property  权限/角色列
     * @param condition 权限条件
     * @param <T>       权限/角色类型
     * @return {@link IUserRolePermMatcher} 条件组装器
     */
    @Override
    public <T> IUserRolePermMatcher eq(Function<SecureUser<?>, Set<T>> property, T condition) {
        if (success) {
            this.update(property.apply(user).contains(condition));
        }
        return this;
    }

    /**
     * 条件组装器 且  任一条件包含匹配
     *
     * @param property   权限/角色列
     * @param conditions 权限条件
     * @param <T>        权限/角色类型
     * @return {@link IUserRolePermMatcher} 条件组装器
     */
    @SafeVarargs
    @Override
    public final <T> IUserRolePermMatcher any(Function<SecureUser<?>, Set<T>> property, T... conditions) {
        if (success) {
            Set<T> roleOrPerms = property.apply(user);
            boolean anyMatch = false;
            for (T condition : conditions) {
                if (roleOrPerms.contains(condition)) {
                    anyMatch = true;
                    break;
                }
            }
            this.update(anyMatch);
        }
        return this;
    }

    /**
     * 条件组装器 且  所有条件都包含匹配
     *
     * @param property   权限/角色列
     * @param conditions 条件权限
     * @param <T>        权限/角色类型
     * @return {@link IUserRolePermMatcher} 条件组装器
     */
    @Override
    @SafeVarargs
    public final <T> IUserRolePermMatcher all(Function<SecureUser<?>, Set<T>> property, T... conditions) {
        if (success) {
            Set<T> roleOrPerms = property.apply(user);
            this.update(roleOrPerms.containsAll(List.of(conditions)));
        }
        return this;
    }

    /**
     * 条件组装器 且
     *
     * @param and 条件组装器
     * @return {@link IUserRolePermMatcher} 条件组装器
     */
    @Override
    public IUserRolePermMatcher and(Consumer<IRolePermMatcher<?>> and) {
        if (success) {
            RolePermMatcher rolePermMatcher = new RolePermMatcher(user);
            and.accept(rolePermMatcher);
            this.update(rolePermMatcher.match());
        }
        return this;
    }

    /**
     * 条件组装器 或
     *
     * @param or 条件组装器
     * @return {@link IUserRolePermMatcher} 条件组装器
     */
    @Override
    public IUserRolePermMatcher or(Consumer<IRolePermMatcher<?>> or) {
        if (orFirst || !success) {
            RolePermMatcher rolePermMatcher = new RolePermMatcher(user);
            or.accept(rolePermMatcher);
            this.update(rolePermMatcher.match());
        }
        return this;
    }

    @Override
    public IUserRolePermMatcher custom(Function<IRolePermMatcher<?>, Boolean> custom) {
        Boolean isSuccess = custom.apply(this);
        if (isSuccess != null) {
            this.update(isSuccess);
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> SecureUser<T> getUser() {
        return (SecureUser<T>) user;
    }

    /**
     * 非黑名单用户 且 匹配成功
     *
     * @return boolean 是否匹配成功 true:匹配成功 false:匹配失败
     */
    @Override
    public boolean match() {
        return !user.getSubRoles().contains(Roles.BLACK_LIST) && success;
    }

    /**
     * 获取用户匹配结果 与用户信息
     *
     * @return {@link UserMatch} 用户匹配结果 与用户信息
     */
    @Override
    public UserMatch userMatch() {
        return UserMatch.builder()
                .secureUser(user)
                .success(match())
                .build();
    }


    private void update(boolean success) {
        this.success = success;
        this.orFirst = false;
    }


}

```

# com.medusa.gruul.common.security.resource.extension.RoleTask

**文件路径**: `resource\extension\RoleTask.java`

## 代码文档
///
角色匹配任务

@author 张治保
date 2022/8/11
 
///

///
当拥有roles中的任意一个角色时执行 consumer 消费任务

@param consumer 消费任务
@param roles    角色列表
@return RoleTask
     
///

///
当是匿名用户访问是的任务

@param anonymousTask 匿名任务
@return RoleTask
     
///

///
当是超级管理员时执行

@param task 任务
@return RoleTask
     
///

///
当是自定义超级管理员时执行

@param task 任务
@return RoleTask
     
///

///
当是超级管理员或自定义超级管理员时执行

@param task 任务
@return RoleTask
     
///

///
当是管理员时执行

@param task 任务
@return RoleTask
     
///

///
当是自定义管理员时执行

@param task 任务
@return RoleTask
     
///

///
当是管理员或自定义管理员时执行

@param task 任务
@return RoleTask
     
///

///
当是用户时执行

@param task 任务
@return RoleTask
     
///

///
当用户没有匹配到任何任务时 执行

@param task 任务
@return RoleTask
     
///

///
执行任务 并返回可能为空的用户信息
当用户匿名访问为空 需要使用方自行判断

@return SecureUser
@deprecated 匿名用户也不会为空, 请使用 {@link #getUser()}}
使用{@link SecureUser#isAnonymous()}判断是否是匿名用户
     
///

///
@return SecureUser 用户信息 请使用 {@link SecureUser#isAnonymous()}判断是否是匿名用户
     
///


## 文件结构
```
项目根目录
└── resource\extension
    └── RoleTask.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.resource.extension;

import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import io.vavr.control.Option;

import java.util.function.Consumer;

/**
 * 角色匹配任务
 *
 * @author 张治保
 * date 2022/8/11
 */
public final class RoleTask {


    private final IUserRolePermMatcher matcher;


    public RoleTask() {
        matcher = ISecurity.matcher();

    }

    /**
     * 当拥有roles中的任意一个角色时执行 consumer 消费任务
     *
     * @param consumer 消费任务
     * @param roles    角色列表
     * @return RoleTask
     */
    public <T> RoleTask when(Consumer<SecureUser<T>> consumer, Roles... roles) {
        matcher.or(
                rp -> rp.any(SecureUser::getRoles, roles)
                        .and(rp1 -> consumer.accept(matcher.getUser()))
        );
        return this;
    }

    /**
     * 当是匿名用户访问是的任务
     *
     * @param anonymousTask 匿名任务
     * @return RoleTask
     */
    public RoleTask anonymous(Runnable anonymousTask) {
        matcher.or(
                rp -> rp.anonymous()
                        .and(rp1 -> anonymousTask.run())
        );
        return this;
    }

    /**
     * 当是超级管理员时执行
     *
     * @param task 任务
     * @return RoleTask
     */
    public <T> RoleTask ifSuperAdmin(Consumer<SecureUser<T>> task) {
        return when(task, Roles.SUPER_ADMIN);
    }

    /**
     * 当是自定义超级管理员时执行
     *
     * @param task 任务
     * @return RoleTask
     */
    public <T> RoleTask ifCustomSuperAdmin(Consumer<SecureUser<T>> task) {
        return when(task, Roles.SUPER_CUSTOM_ADMIN);
    }

    /**
     * 当是超级管理员或自定义超级管理员时执行
     *
     * @param task 任务
     * @return RoleTask
     */
    public <T> RoleTask ifAnySuperAdmin(Consumer<SecureUser<T>> task) {
        return when(task, Roles.SUPER_ADMIN, Roles.SUPER_CUSTOM_ADMIN);
    }

    /**
     * 当是管理员时执行
     *
     * @param task 任务
     * @return RoleTask
     */
    public <T> RoleTask ifAdmin(Consumer<SecureUser<T>> task) {
        return when(task, Roles.ADMIN);
    }

    /**
     * 当是自定义管理员时执行
     *
     * @param task 任务
     * @return RoleTask
     */
    public <T> RoleTask ifCustomAdmin(Consumer<SecureUser<T>> task) {
        return when(task, Roles.CUSTOM_ADMIN);
    }

    /**
     * 当是管理员或自定义管理员时执行
     *
     * @param task 任务
     * @return RoleTask
     */
    public <T> RoleTask ifAnyShopAdmin(Consumer<SecureUser<T>> task) {
        return when(task, Roles.ADMIN, Roles.CUSTOM_ADMIN);
    }


    public <T> RoleTask ifAnySupplierAdmin(Consumer<SecureUser<T>> task) {
        return when(task, Roles.SUPPLIER_ADMIN, Roles.SUPPLIER_CUSTOM_ADMIN);
    }

    /**
     * 当是用户时执行
     *
     * @param task 任务
     * @return RoleTask
     */
    public <T> RoleTask ifUser(Consumer<SecureUser<T>> task) {
        return when(task, Roles.USER);
    }

    /**
     * 当用户没有匹配到任何任务时 执行
     *
     * @param task 任务
     * @return RoleTask
     */
    public <T> RoleTask other(Consumer<SecureUser<T>> task) {
        matcher.or(rp -> task.accept(matcher.getUser()));
        return this;
    }


    /**
     * 执行任务 并返回可能为空的用户信息
     * 当用户匿名访问为空 需要使用方自行判断
     *
     * @return SecureUser
     * @deprecated 匿名用户也不会为空, 请使用 {@link #getUser()}}
     * 使用{@link SecureUser#isAnonymous()}判断是否是匿名用户
     */
    @Deprecated
    public <T> Option<SecureUser<T>> run() {
        SecureUser<T> user = getUser();
        if (user.isAnonymous()) {
            return Option.none();
        }
        return Option.of(user);
    }

    /**
     * @return SecureUser 用户信息 请使用 {@link SecureUser#isAnonymous()}判断是否是匿名用户
     */
    public <T> SecureUser<T> getUser() {
        return matcher.getUser();
    }


}

```

# com.medusa.gruul.common.security.resource.helper.ISecurity

**文件路径**: `resource\helper\ISecurity.java`

## 代码文档
///
安全服务工具类

@author 张治保
 
///

///
匿名访问类型
     
///

///
token头
     
///

///
在 authentication 下 执行操作逻辑,并返回一个值

@param authentication 认证信息
@param supplier       执行的任务
@return 任务执行结果
     
///

///
在 authentication 下 执行操作逻辑

@param authentication 认证信息
@param runnable       执行任务
     
///

///
获取认证信息
已认证 OAuth2Authentication
未认证  AnonymousAuthenticationToken

@param allowAnonymous 是否允许匿名访问
@return 认证信息
     
///

///
获取可能为空的用户信息

@return option<user>
     
///

///
必须要有登陆认证的用户信息

@return 当前登陆的用户信息
     
///

///
必须要有登陆认证的用户信息

@param authentication 认证信息
@return 当前登陆的用户信息
     
///

///
获取当前用户信息

@return 用户信息
     
///

///
根据指定的认证信息 获取当前用户信息

@param authentication 认证信息
@return 用户信息
     
///

///
是否是匿名访问

@param authentication 认证信息
@return 是否是匿名访问
     
///

///
是否是匿名访问

@return 是否是匿名访问
     
///

///
是否已认证

@return 是否已认证
     
///

///
每个角色对应不同的处理任务

@return {@link RoleTask}
     
///

///
获取 用户与权限匹配器

@return 用户与权限匹配器
     
///

///
获取 用户与权限匹配器

@param authentication 认证信息
@return 用户与权限匹配器
     
///

///
是否匹配到任意角色

@param roles 角色列表
@return 是否匹配到任意角色
     
///

///
用户重置 仅刷新当前客户端和店铺 id（用户资料更新 触发用户重新加载个人最新信息 ）

@param type    客户端类型
@param shopId  店铺 id
@param userIds 用户id列表
     
///

///
用户重置 重置所有客户端、登录的用户信息 （用户资料更新 触发用户重新加载个人最新信息 ）

@param userIds 用户id列表
     
///

///
把登录状态的用户踢出

@param type    客户端类型
@param shopId  店铺id
@param userIds 需要踢出的用户id列表
@param message 提示信息
     
///

///
更新用户登录状态状态
     
///

///
更新用户登录状态状态
     
///

///
批量店铺用户下线

@param type    客户端类型
@param shopIds 店铺id列表
     
///

///
禁用店铺id下的部分指定用户

@param type    客户端类型
@param shopId  店铺id
@param userIds 用户id列表
     
///

///
禁用店铺id下的部分指定用户

@param type    客户端类型
@param shopId  店铺id
@param userIds 用户id列表
     
///

///
具体的 token 下线

@param type    客户端类型
@param shopId  店铺id
@param userId  用户id
@param tokenId token id
     
///

///
具体的 token 下线

@param key token key
     
///

///
获取所有匹配到的用户 TokenKey

@param type    客户端类型
@param shopId  店铺id
@param userIds 用户id列表
@return 匹配到的所有key
     
///

///
获取所有匹配到的用户 TokenKey

@param type    客户端类型
@param shopId  店铺id
@param userIds 用户id列表
@return 匹配到的所有key
     
///

///
新增店铺id下的用户 上线

@param onlineUser 用户上线参数
     
///

///
获取缓存的 tokenId

@param tokenKey token key
@return tokenId 可能为空
     
///

///
生成 token key 前缀

@param tokenKey token key
@return token key 前缀
     
///

///
生成 token key 匹配 前缀

@param clientType 客户端类型
@param shopId     店铺id
@return token key 匹配 前缀
     
///

///
当前用户名与用户id后六位生成用户昵称
当前用户名不为空 返回当前用户名 否则使用用户id生成昵称

@param current 当前用户名
@param userId  用户id
@return 用户昵称 option
     
///

///
根据用户id后六位生成用户昵称

@param userId 用户id
@return 用户昵称 option
     
///

///
user信息转 principal 加密后的用户数据 用户主要信息

@param secureUser 用户信息
@return principal 加密后的用户数据 用户主要信息
     
///

///
principal 加密后的用户数据 用户主要信息

@param principal 加密后的用户数据 用户主要信息
@return user信息
     
///

///
用于存储、获取额外数据的类型
     
///

///
安全用户 额外数据的类型 默认 object 类型
         
///

///
设置安全用户额外数据的类型

@param clazz 额外数据的类型
@param <T>   额外数据的类型 泛型
         
///

///
获取安全用户额外数据的类型

@return TypeReference<SecureUser < ?>>
         
///


## 文件结构
```
项目根目录
└── resource\helper
    └── ISecurity.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.resource.helper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.TypeReference;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.bean.CacheToken;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.bean.TokenKey;
import com.medusa.gruul.common.security.model.constant.SecureConst;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.model.enums.TokenState;
import com.medusa.gruul.common.security.resource.extension.IUserRolePermMatcher;
import com.medusa.gruul.common.security.resource.extension.RolePermMatcher;
import com.medusa.gruul.common.security.resource.extension.RoleTask;
import com.medusa.gruul.common.security.resource.model.OnlineUserParam;
import com.medusa.gruul.common.system.model.model.ClientType;
import io.vavr.control.Option;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 安全服务工具类
 *
 * @author 张治保
 */
public interface ISecurity {


    /**
     * 匿名访问类型
     */
    Class<? extends Authentication> ANONYMOUS_CLASS = AnonymousAuthenticationToken.class;

    /**
     * token头
     */
    String HEADER = "Authorization";


    /**
     * 在 authentication 下 执行操作逻辑,并返回一个值
     *
     * @param authentication 认证信息
     * @param supplier       执行的任务
     * @return 任务执行结果
     */
    static <T> T withAuthentication(Authentication authentication, Supplier<T> supplier) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        try {
            return supplier.get();
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * 在 authentication 下 执行操作逻辑
     *
     * @param authentication 认证信息
     * @param runnable       执行任务
     */
    static void withAuthentication(Authentication authentication, Runnable runnable) {
        ISecurity.withAuthentication(
                authentication,
                () -> {
                    runnable.run();
                    return null;
                }
        );
    }

    /**
     * 获取认证信息
     * 已认证 OAuth2Authentication
     * 未认证  AnonymousAuthenticationToken
     *
     * @param allowAnonymous 是否允许匿名访问
     * @return 认证信息
     */
    static Authentication getAuthentication(boolean allowAnonymous) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!allowAnonymous && isAnonymous(authentication)) {
            throw SecureCodes.NEED_LOGIN.exception();
        }
        return authentication;
    }


    /**
     * 获取可能为空的用户信息
     *
     * @return option<user>
     */
    static <T> Option<SecureUser<T>> userOpt() {
        SecureUser<T> user = ISecurity.secureUser();
        if (user.isAnonymous()) {
            return Option.none();
        }
        return Option.of(user);
    }

    /**
     * 必须要有登陆认证的用户信息
     *
     * @return 当前登陆的用户信息
     */
    static <T> SecureUser<T> userMust() {
        Authentication authentication = getAuthentication(false);
        if (authentication == null) {
            throw SecureCodes.NEED_LOGIN.exception();
        }
        return userMust(authentication);
    }

    /**
     * 必须要有登陆认证的用户信息
     *
     * @param authentication 认证信息
     * @return 当前登陆的用户信息
     */
    static <T> SecureUser<T> userMust(@NonNull Authentication authentication) {
        SecureUser<T> user = ISecurity.matcher(authentication)
                .getUser();
        if (user.isAnonymous()) {
            throw SecureCodes.NEED_LOGIN.exception();
        }
        return user;
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    static <T> SecureUser<T> secureUser() {
        return ISecurity.secureUser(ISecurity.getAuthentication(true));
    }

    /**
     * 根据指定的认证信息 获取当前用户信息
     *
     * @param authentication 认证信息
     * @return 用户信息
     */
    static <T> SecureUser<T> secureUser(@NonNull Authentication authentication) {
        return ISecurity.matcher(authentication)
                .getUser();
    }

    /**
     * 是否是匿名访问
     *
     * @param authentication 认证信息
     * @return 是否是匿名访问
     */
    static boolean isAnonymous(Authentication authentication) {
        return authentication == null || ANONYMOUS_CLASS.isAssignableFrom(authentication.getClass());
    }

    /**
     * 是否是匿名访问
     *
     * @return 是否是匿名访问
     */
    static boolean isAnonymous() {
        return ISecurity.matcher().getUser().isAnonymous();
    }

    /**
     * 是否已认证
     *
     * @return 是否已认证
     */
    static boolean isAuthenticated() {
        return !ISecurity.isAnonymous();
    }

    /**
     * 每个角色对应不同的处理任务
     *
     * @return {@link RoleTask}
     */
    static RoleTask match() {
        return new RoleTask();
    }

    /**
     * 获取 用户与权限匹配器
     *
     * @return 用户与权限匹配器
     */
    static IUserRolePermMatcher matcher() {
        return ISecurity.matcher(ISecurity.getAuthentication(true));
    }

    /**
     * 获取 用户与权限匹配器
     *
     * @param authentication 认证信息
     * @return 用户与权限匹配器
     */
    static IUserRolePermMatcher matcher(Authentication authentication) {
        return new RolePermMatcher(authentication);
    }

    /**
     * 是否匹配到任意角色
     *
     * @param roles 角色列表
     * @return 是否匹配到任意角色
     */
    static boolean anyRole(Roles... roles) {
        return ISecurity.matcher()
                .anyRole(roles)
                .match();
    }


    /**
     * 用户重置 仅刷新当前客户端和店铺 id（用户资料更新 触发用户重新加载个人最新信息 ）
     *
     * @param type    客户端类型
     * @param shopId  店铺 id
     * @param userIds 用户id列表
     */
    static void refreshUsers(ClientType type, Long shopId, Set<Long> userIds) {
        updateUsersState(
                type,
                shopId,
                userIds,
                TokenState.REFRESH,
                StrUtil.EMPTY
        );
    }

    /**
     * 用户重置 重置所有客户端、登录的用户信息 （用户资料更新 触发用户重新加载个人最新信息 ）
     *
     * @param userIds 用户id列表
     */
    static void refreshUsers(Set<Long> userIds) {
        Set<String> keys = RedisUtil.scan(
                userIds.stream()
                        .map(
                                userId -> RedisUtil.key(
                                        SecureConst.USER_LOGIN_STATE_CHECK,
                                        '*',
                                        '*',
                                        userId.toString(),
                                        '*'
                                )
                        ).collect(Collectors.toSet())
        );
        if (CollUtil.isEmpty(keys)) {
            return;
        }
        updateUsersState(keys, TokenState.REFRESH, StrUtil.EMPTY);
    }


    /**
     * 把登录状态的用户踢出
     *
     * @param type    客户端类型
     * @param shopId  店铺id
     * @param userIds 需要踢出的用户id列表
     * @param message 提示信息
     */
    static void kickUsers(ClientType type, Long shopId, Set<Long> userIds, String message) {
        updateUsersState(
                type,
                shopId,
                userIds,
                TokenState.KICK,
                message
        );
    }

    /**
     * 更新用户登录状态状态
     */
    private static void updateUsersState(ClientType type, Long shopId, Set<Long> userIds, TokenState state, String message) {
        Collection<String> keys = allMatchedKeys(type, shopId, userIds);
        if (CollUtil.isEmpty(keys)) {
            return;
        }
        updateUsersState(keys, state, message);
    }

    /**
     * 更新用户登录状态状态
     */
    private static void updateUsersState(Collection<String> keys, TokenState state, String message) {
        Map<String, Object> value = Map.of(
                CacheToken.STATE_FILED, state.name(),
                CacheToken.MESSAGE_FILED, message
        );
        Map<String, Map<String, Object>> keyWithHashValueMap = MapUtil.newHashMap(keys.size());
        for (String key : keys) {
            keyWithHashValueMap.put(key, value);
        }
        RedisUtil.batchPutIfPresent(keyWithHashValueMap);
    }


    /**
     * 批量店铺用户下线
     *
     * @param type    客户端类型
     * @param shopIds 店铺id列表
     */
    static void offlineAllUsers(ClientType type, Set<Long> shopIds) {
        if (CollUtil.isEmpty(shopIds)) {
            return;
        }
        Set<String> patterns = shopIds.stream()
                .map(shopId -> ISecurity.tokenPattern(type, shopId))
                .collect(Collectors.toSet());
        RedisUtil.matchThenDelete(patterns);
    }

    /**
     * 禁用店铺id下的部分指定用户
     *
     * @param type    客户端类型
     * @param shopId  店铺id
     * @param userIds 用户id列表
     */
    static void offlineUsers(ClientType type, Long shopId, Long... userIds) {
        ISecurity.offlineUsers(type, shopId, Set.of(userIds));
    }

    /**
     * 禁用店铺id下的部分指定用户
     *
     * @param type    客户端类型
     * @param shopId  店铺id
     * @param userIds 用户id列表
     */
    static void offlineUsers(ClientType type, Long shopId, Collection<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return;
        }
        RedisUtil.matchThenDelete(
                allUserPatternKeys(type, shopId, userIds)
        );
    }


    /**
     * 具体的 token 下线
     *
     * @param type    客户端类型
     * @param shopId  店铺id
     * @param userId  用户id
     * @param tokenId token id
     */
    static void offlineUser(ClientType type, Long shopId, Long userId, String tokenId) {
        offlineUser(new TokenKey(type, shopId, userId, tokenId));
    }

    /**
     * 具体的 token 下线
     *
     * @param key token key
     */
    static void offlineUser(TokenKey key) {
        RedisUtil.delete(ISecurity.tokenKey(key));
    }

    /**
     * 获取所有匹配到的用户 TokenKey
     *
     * @param type    客户端类型
     * @param shopId  店铺id
     * @param userIds 用户id列表
     * @return 匹配到的所有key
     */
    private static Set<String> allMatchedKeys(ClientType type, Long shopId, Collection<Long> userIds) {
        return RedisUtil.scan(
                allUserPatternKeys(type, shopId, userIds)
        );
    }

    /**
     * 获取所有匹配到的用户 TokenKey
     *
     * @param type    客户端类型
     * @param shopId  店铺id
     * @param userIds 用户id列表
     * @return 匹配到的所有key
     */
    private static Set<String> allUserPatternKeys(ClientType type, Long shopId, Collection<Long> userIds) {
        return userIds.stream()
                .map(userId -> ISecurity.tokenKey(
                        new TokenKey(
                                type,
                                shopId,
                                userId,
                                "*"
                        )
                ))
                .collect(Collectors.toSet());
    }


    /**
     * 新增店铺id下的用户 上线
     *
     * @param onlineUser 用户上线参数
     */
    @SuppressWarnings("unchecked")
    static void onlineUser(OnlineUserParam onlineUser) {
        String tokenKey = ISecurity.tokenKey(
                new TokenKey(
                        onlineUser.getClientType(),
                        onlineUser.getShopId(),
                        onlineUser.getUserId(),
                        onlineUser.getTokenId()
                )
        );
        RedisUtil.executePipelined(
                operations -> {
                    HashOperations<String, String, Object> hashOperations = operations.opsForHash();
                    hashOperations.put(tokenKey, CacheToken.STATE_FILED, TokenState.ONLINE.name());
                    operations.expireAt(tokenKey, onlineUser.refreshTokenExpire());
                }
        );
    }

    /**
     * 获取缓存的 tokenId
     *
     * @param tokenKey token key
     * @return tokenId 可能为空
     */
    static Option<CacheToken> getCacheToken(TokenKey tokenKey) {
        return Option.of(
                RedisUtil.getCacheMap(
                        ISecurity.tokenKey(tokenKey),
                        CacheToken.class
                )
        );
    }

    /**
     * 生成 token key 前缀
     *
     * @param tokenKey token key
     * @return token key 前缀
     */
    static String tokenKey(TokenKey tokenKey) {
        return RedisUtil.key(
                SecureConst.USER_LOGIN_STATE_CHECK,
                tokenKey.getClientType().name(),
                tokenKey.getShopId().toString(),
                tokenKey.getUserId().toString(),
                tokenKey.getTokenId()
        );
    }


    /**
     * 生成 token key 匹配 前缀
     *
     * @param clientType 客户端类型
     * @param shopId     店铺id
     * @return token key 匹配 前缀
     */
    static String tokenPattern(ClientType clientType, Long shopId) {
        return RedisUtil.key(
                SecureConst.USER_LOGIN_STATE_CHECK,
                clientType.name(),
                shopId.toString(),
                "*"
        );
    }

    /**
     * 当前用户名与用户id后六位生成用户昵称
     * 当前用户名不为空 返回当前用户名 否则使用用户id生成昵称
     *
     * @param current 当前用户名
     * @param userId  用户id
     * @return 用户昵称 option
     */
    static Option<String> generateNickName(String current, Long userId) {
        return Option.when(StrUtil.isNotBlank(current), current)
                .orElse(
                        ISecurity.generateNickName(userId)
                );
    }

    /**
     * 根据用户id后六位生成用户昵称
     *
     * @param userId 用户id
     * @return 用户昵称 option
     */
    static Option<String> generateNickName(Long userId) {
        return Option.of(userId)
                .map(id -> {
                    String idStr = String.valueOf(id);
                    return SecureConst.DEFAULT_USER_NICKNAME_PREFIX + StrUtil.subSufByLength(idStr, 6);
                });
    }

    /**
     * user信息转 principal 加密后的用户数据 用户主要信息
     *
     * @param secureUser 用户信息
     * @return principal 加密后的用户数据 用户主要信息
     */
    static byte[] toPrincipal(SecureUser<?> secureUser) {
        return JSONB.toBytes(
                secureUser,
                JSONWriter.Feature.FieldBased,
                JSONWriter.Feature.BeanToArray,
                JSONWriter.Feature.ReferenceDetection,
                JSONWriter.Feature.NotWriteDefaultValue,
                JSONWriter.Feature.NotWriteEmptyArray
        );
    }


    /**
     * principal 加密后的用户数据 用户主要信息
     *
     * @param principal 加密后的用户数据 用户主要信息
     * @return user信息
     */
    static SecureUser<?> toUser(byte[] principal) {
        return JSONB.parseObject(
                principal,
                ExtraRef.get(),
                JSONReader.Feature.FieldBased,
                JSONReader.Feature.SupportArrayToBean,
                JSONReader.Feature.UseDefaultConstructorAsPossible,
                JSONReader.Feature.UseNativeObject
        );
    }


    /**
     * 用于存储、获取额外数据的类型
     */
    class ExtraRef {

        /**
         * 安全用户 额外数据的类型 默认 object 类型
         */
        private static TypeReference<SecureUser<?>> typeReference = new TypeReference<>() {
        };


        /**
         * 设置安全用户额外数据的类型
         *
         * @param clazz 额外数据的类型
         * @param <T>   额外数据的类型 泛型
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        public static <T> void set(Class<T> clazz) {
            typeReference = (TypeReference) new TypeReference<SecureUser<T>>(clazz) {
            };
        }


        /**
         * 获取安全用户额外数据的类型
         *
         * @return TypeReference<SecureUser < ?>>
         */
        public static TypeReference<SecureUser<?>> get() {
            return typeReference;
        }


    }
}


```

# com.medusa.gruul.common.security.resource.helper.Security

**文件路径**: `resource\helper\Security.java`

## 代码文档
///
@author 张治保
date 2022/2/25
 
///

///
角色过度器
     
///

///
开发者 角色
     
///

///
平台管理员
     
///

///
平台自定义管理员
     
///

///
商家管理员
     
///

///
商家自定义管理员
     
///

///
消费者
     
///

///
获取用户角色集合 func
     
///

///
获取用户副角色集合 func
     
///

///
获取用户权限集合 func
     
///

///
用户角色权限匹配器

@return IRolePermMatcher 角色权限匹配器
     
///

///
IRolePermMatcher 消费器 用于给 springEl表达式使用

@return RolePermConsumer 消费器
     
///

///
是否已认证
     
///

///
是否有平台权限 管理员直接放行  自定义管理员有权限放行

@param perms 权限列表
@return 是否有权限
     
///

///
是否有商家端 权限
管理员直接放行
自定义管理员 有权限放行
     
///

///
任意权限

@param platformPerms 平台权限
@param shopPerms     商家权限
     
///

///
管理员直接放行  自定义管理员有权限放行

@param platform 是否是平台 true 平台管理员  false 商家管理员
@param perms    权限列表
@return 是否有权限
     
///


## 文件结构
```
项目根目录
└── resource\helper
    └── Security.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.resource.helper;

import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.extension.IUserRolePermMatcher;
import com.medusa.gruul.common.security.resource.extension.RolePermConsumer;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Function;

/**
 * @author 张治保
 * date 2022/2/25
 */
@Component("S")
public final class Security {

    /**
     * 角色过度器
     */
    public static final Roles R = Roles.USER;

    /**
     * 开发者 角色
     */
    public static final Roles DEVELOPER = Roles.DEVELOPER;

    /**
     * 平台管理员
     */
    public static final Roles PLATFORM_ADMIN = Roles.SUPER_ADMIN;

    /**
     * 平台自定义管理员
     */
    public static final Roles PLATFORM_CUSTOM_ADMIN = Roles.SUPER_CUSTOM_ADMIN;

    /**
     * 商家管理员
     */
    public static final Roles SHOP_ADMIN = Roles.ADMIN;

    /**
     * 商家自定义管理员
     */
    public static final Roles SHOP_CUSTOM_ADMIN = Roles.CUSTOM_ADMIN;

    /**
     * 消费者
     */
    public static final Roles USER = Roles.USER;


    /**
     * 获取用户角色集合 func
     */
    public static final Function<SecureUser<?>, Set<Roles>> ROLES = SecureUser::getRoles;

    /**
     * 获取用户副角色集合 func
     */
    public static final Function<SecureUser<?>, Set<Roles>> SUB_ROLES = SecureUser::getSubRoles;

    /**
     * 获取用户权限集合 func
     */
    public static final Function<SecureUser<?>, Set<String>> PERMS = SecureUser::getPerms;


    /**
     * 用户角色权限匹配器
     *
     * @return IRolePermMatcher 角色权限匹配器
     */
    public IUserRolePermMatcher matcher() {
        return ISecurity.matcher();
    }

    /**
     * IRolePermMatcher 消费器 用于给 springEl表达式使用
     *
     * @return RolePermConsumer 消费器
     */
    public RolePermConsumer consumer() {
        return new RolePermConsumer();
    }

    /**
     * 是否已认证
     */
    public boolean isAuthenticated() {
        return ISecurity.isAuthenticated();
    }


    /**
     * 是否有平台权限 管理员直接放行  自定义管理员有权限放行
     *
     * @param perms 权限列表
     * @return 是否有权限
     */
    public boolean platformPerm(String... perms) {
        return this.hasPerm(true, perms);
    }

    /**
     * 是否有商家端 权限
     * 管理员直接放行
     * 自定义管理员 有权限放行
     */
    public boolean shopPerm(String... perms) {
        return this.hasPerm(false, perms);
    }

    /**
     * 任意权限
     *
     * @param platformPerms 平台权限
     * @param shopPerms     商家权限
     */
    public boolean anyPerm(String[] platformPerms, String[] shopPerms) {
        return this.matcher()
                .anyRole(Roles.SUPER_ADMIN, Roles.ADMIN)
                .or(matcher -> matcher.role(Roles.SUPER_CUSTOM_ADMIN).anyPerm(platformPerms))
                .or(matcher -> matcher.role(Roles.CUSTOM_ADMIN).anyPerm(shopPerms))
                .match();
    }

    /**
     * 管理员直接放行  自定义管理员有权限放行
     *
     * @param platform 是否是平台 true 平台管理员  false 商家管理员
     * @param perms    权限列表
     * @return 是否有权限
     */
    private boolean hasPerm(boolean platform, String... perms) {
        return ISecurity.matcher()
                .role(platform ? Roles.SUPER_ADMIN : Roles.ADMIN)
                .or(matcher -> matcher.role(platform ? Roles.SUPER_CUSTOM_ADMIN : Roles.CUSTOM_ADMIN).anyPerm(perms))
                .match();
    }


}

```

# com.medusa.gruul.common.security.resource.model.OnlineUserParam

**文件路径**: `resource\model\OnlineUserParam.java`

## 代码文档
///
用于用户上线的参数

@author 张治保
@since 2023/11/3
 
///

///
客户端类型
     
///

///
店铺 id
     
///

///
用户 id
     
///

///
tokenId
     
///

///
refreshToken 过期时间
     
///

///
refreshToken 过期时间 转换为 Instant

@return Instant
     
///


## 文件结构
```
项目根目录
└── resource\model
    └── OnlineUserParam.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.resource.model;

import com.medusa.gruul.common.system.model.model.ClientType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 用于用户上线的参数
 *
 * @author 张治保
 * @since 2023/11/3
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@Accessors(chain = true)
public final class OnlineUserParam implements Serializable {
    /**
     * 客户端类型
     */
    private ClientType clientType;

    /**
     * 店铺 id
     */
    private Long shopId;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * tokenId
     */
    private String tokenId;

    /**
     * refreshToken 过期时间
     */
    private LocalDateTime refreshTokenExpireAt;

    /**
     * refreshToken 过期时间 转换为 Instant
     *
     * @return Instant
     */
    public Instant refreshTokenExpire() {
        return refreshTokenExpireAt.atZone(ZoneId.systemDefault()).toInstant();
    }


}

```

# com.medusa.gruul.common.security.resource.model.SecureUserAuthentication

**文件路径**: `resource\model\SecureUserAuthentication.java`

## 代码文档
///
@author 张治保
@since 2023/11/2
 
///

///
token id
     
///

///
用户信息
     
///

///
创建一个已认证对象

@param tokenId token id
@param user    用户信息
@return 已认证对象
     
///


## 文件结构
```
项目根目录
└── resource\model
    └── SecureUserAuthentication.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.resource.model;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

/**
 * @author 张治保
 * @since 2023/11/2
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public final class SecureUserAuthentication implements Authentication {
    /**
     * token id
     */
    private String tokenId;

    /**
     * 用户信息
     */
    private SecureUser<?> user;

    /**
     * 创建一个已认证对象
     *
     * @param tokenId token id
     * @param user    用户信息
     * @return 已认证对象
     */
    public static SecureUserAuthentication of(String tokenId, SecureUser<?> user) {
        return new SecureUserAuthentication(tokenId, user);
    }

    public String tokenId() {
        return tokenId;
    }

    public SecureUser<?> user() {
        return user;
    }

    @Override
    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    public Object getCredentials() {
        return StrUtil.EMPTY;
    }

    @Override
    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    public Object getDetails() {
        return StrUtil.EMPTY;
    }

    @Override
    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    public Object getPrincipal() {
        return StrUtil.EMPTY;
    }

    @Override
    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    public String getName() {
        return StrUtil.EMPTY;
    }
}

```

# com.medusa.gruul.common.security.resource.properties.SecurityClientProperties

**文件路径**: `resource\properties\SecurityClientProperties.java`

## 代码文档
///
@author 张治保
date 2022/2/25
 
///

///
是否允许iframe访问  会添加响应头 x-frame-options deny
     
///

///
jwt payload 中的key 用于加密用户信息
     
///

///
公钥 用于 jwt验签
     
///

///
把uri作为权限校验
     
///

///
设置额外数据类型

@param extraType 额外数据类型
     
///

///
获取 payloadKey

@return payloadKey
     
///

///
是否启用
         
///

///
uri白名单 配置了就会使用 uri的权限校验方式
         
///


## 文件结构
```
项目根目录
└── resource\properties
    └── SecurityClientProperties.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.resource.properties;

import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.helper.SecureHelper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;
import java.io.Serializable;

/**
 * @author 张治保
 * date 2022/2/25
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.security.client")
public class SecurityClientProperties {


    /**
     * 是否允许iframe访问  会添加响应头 x-frame-options deny
     */
    private boolean enableFrameOptions = true;

    /**
     * jwt payload 中的key 用于加密用户信息
     */
    private String payloadKey = "MbXubWPr4DQLFFU3T1RdjA==";

    /**
     * 公钥 用于 jwt验签
     */
    private String publicKey;

    /**
     * 把uri作为权限校验
     */
    private UriPermission uriPermission = new UriPermission();


    /**
     * 设置额外数据类型
     *
     * @param extraType 额外数据类型
     */
    public void setExtraType(Class<?> extraType) {
        ISecurity.ExtraRef.set(extraType);
    }

    /**
     * 获取 payloadKey
     *
     * @return payloadKey
     */
    public SecretKey payloadKey() {
        return SecureHelper.aesFromKey(getPayloadKey()).getSecretKey();
    }


    @Getter
    @Setter
    public static class UriPermission implements Serializable {

        /**
         * 是否启用
         */
        private boolean enable = false;

        /**
         * uri白名单 配置了就会使用 uri的权限校验方式
         */
        private String[] permits = new String[0];

    }
}

```

# com.medusa.gruul.common.security.resource.tool.ResponseUtil

**文件路径**: `resource\tool\ResponseUtil.java`

## 代码文档
///
响应处理 工具类

@author 张治保
date 2022/2/28
 
///

///
响应数据

@param response 响应
@throws IOException IO 异常
     
///

///
直接响应 跳过 消息转换器

@param response response
@param value    响应数据
     
///


## 文件结构
```
项目根目录
└── resource\tool
    └── ResponseUtil.java
```

## 完整代码
```java
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

```

# com.medusa.gruul.common.security.resource.tool.SecurityChainConfig

**文件路径**: `resource\tool\SecurityChainConfig.java`

## 代码文档
///
@author 张治保
@since 2023/11/2
 
///

///
config HttpSecurity

@param http HttpSecurity
     
///


## 文件结构
```
项目根目录
└── resource\tool
    └── SecurityChainConfig.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.resource.tool;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * @author 张治保
 * @since 2023/11/2
 */
public interface SecurityChainConfig {

    /**
     * config HttpSecurity
     *
     * @param http HttpSecurity
     */
    void config(HttpSecurity http);

}

```

# com.medusa.gruul.common.security.resource.tool.jwt.ClientJwtSecretKey

**文件路径**: `tool\jwt\ClientJwtSecretKey.java`

## 代码文档
///
认证客户端jwt秘钥数据

@author 张治保
@since 2023/11/30
 
///

///
jwt  验签算法公钥
     
///

///
jwt payload 加密算法
     
///


## 文件结构
```
项目根目录
└── tool\jwt
    └── ClientJwtSecretKey.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.resource.tool.jwt;

import cn.hutool.crypto.symmetric.AES;
import com.medusa.gruul.global.model.helper.SecureHelper;

import java.security.PublicKey;

/**
 * 认证客户端jwt秘钥数据
 *
 * @author 张治保
 * @since 2023/11/30
 */
public final class ClientJwtSecretKey implements IClientSecretKey {

    /**
     * jwt  验签算法公钥
     */
    private final PublicKey publicKey;

    /**
     * jwt payload 加密算法
     */
    private final AES payloadSecret;

    public ClientJwtSecretKey(String publicKey, String payloadKey) {
        this.publicKey = SecureHelper.esPublicKey(publicKey);
        this.payloadSecret = SecureHelper.aesFromKey(payloadKey);
    }

    @Override
    public PublicKey verifyKey() {
        return publicKey;
    }

    @Override
    public AES payload() {
        return payloadSecret;
    }
}
```

# com.medusa.gruul.common.security.resource.tool.jwt.IClientSecretKey

**文件路径**: `tool\jwt\IClientSecretKey.java`

## 代码文档
///
jwt 加解密完整key 数据

@author 张治保
@since 2023/11/29
 
///

///
公钥 用于 jwt数据验签

@return PublicKey
     
///

///
payload key 用于加解密 payload信息

@return aes crypto
     
///


## 文件结构
```
项目根目录
└── tool\jwt
    └── IClientSecretKey.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.resource.tool.jwt;

import cn.hutool.crypto.symmetric.AES;

import java.security.PublicKey;

/**
 * jwt 加解密完整key 数据
 *
 * @author 张治保
 * @since 2023/11/29
 */
public interface IClientSecretKey {

    /**
     * 公钥 用于 jwt数据验签
     *
     * @return PublicKey
     */
    PublicKey verifyKey();

    /**
     * payload key 用于加解密 payload信息
     *
     * @return aes crypto
     */
    AES payload();


}

```

# com.medusa.gruul.common.security.resource.tool.jwt.JwtDecoder

**文件路径**: `tool\jwt\JwtDecoder.java`

## 代码文档
///
jwt token 解码器

@author 张治保
@since 2023/11/2
 
///

///
解码 token

@param tokenType token类型
@param token     token
@return Tuple2<Boolean, Claims> 1.是否已过期 2.解码后的token 信息
     
///

///
解码 token

@param tokenType token类型
@param token     token
@return DecodeToken 解码后的token 信息
     
///

///
token 转换为认证信息

@param token token
@return SecureUserAuthentication 认证信息
     
///

///
token 转换为认证信息

@param token  token
@param logout 是否是登出请求 如果是登出请求 则不校验 token 是否过期
@return SecureUserAuthentication 认证信息
     
///


## 文件结构
```
项目根目录
└── tool\jwt
    └── JwtDecoder.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.resource.tool.jwt;

import cn.hutool.core.date.DateTime;
import com.medusa.gruul.common.security.model.bean.DecodeToken;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.model.enums.TokenType;
import com.medusa.gruul.common.security.resource.exception.SecurityException;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.security.resource.model.SecureUserAuthentication;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.impl.CustomJwtParser;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;

/**
 * jwt token 解码器
 *
 * @author 张治保
 * @since 2023/11/2
 */
@Slf4j
public final class JwtDecoder {

    private final JwtParser parser;
    private final ClientJwtSecretKey clientSecretKey;

    public JwtDecoder(ClientJwtSecretKey clientSecretKey) {
        this.clientSecretKey = clientSecretKey;
        this.parser = new CustomJwtParser(clientSecretKey.verifyKey());
    }

    /**
     * 解码 token
     *
     * @param tokenType token类型
     * @param token     token
     * @return Tuple2<Boolean, Claims> 1.是否已过期 2.解码后的token 信息
     */
    private Tuple2<Boolean, Claims> parse(TokenType tokenType, String token) {
        try {
            return Tuple.of(Boolean.FALSE, (Claims) (parser.parse(token).getPayload()));
        } catch (ExpiredJwtException exception) {
            Claims claims = exception.getClaims();
            return Tuple.of(Boolean.TRUE, claims);
        } catch (Exception exception) {
            log.debug("jwt parse error", exception);
            throw SecurityException.of(tokenType.getInvalid());
        }
    }

    /**
     * 解码 token
     *
     * @param tokenType token类型
     * @param token     token
     * @return DecodeToken 解码后的token 信息
     */
    public DecodeToken decode(TokenType tokenType, String token) {
        Tuple2<Boolean, Claims> claimsTuple = parse(tokenType, token);
        Claims claims = claimsTuple._2();
        TokenType currentType = TokenType.valueOf(claims.get(TokenType.TYPE, String.class));
        if (currentType != tokenType) {
            throw SecurityException.of(SecureCodes.REQUEST_INVALID);
        }
        SecureUser<?> user;
        try {
            user = ISecurity.toUser(
                    clientSecretKey.payload()
                            .decrypt(
                                    claims.get(TokenType.KEY, byte[].class)
                            )
            );
        } catch (Exception exception) {
            log.error("payload decode error", exception);
            throw SecurityException.of(tokenType.getInvalid());
        }
        return new DecodeToken()
                .setExpired(claimsTuple._1())
                .setTokenId(claims.getId())
                .setTokenType(currentType)
                .setSecureUser(user)
                .setIssuedAt(DateTime.of(claims.getIssuedAt()).toLocalDateTime())
                .setExpireAt(DateTime.of(claims.getExpiration()).toLocalDateTime());
    }

    /**
     * token 转换为认证信息
     *
     * @param token token
     * @return SecureUserAuthentication 认证信息
     */
    public SecureUserAuthentication authentication(String token) {
        return authentication(token, false);
    }

    /**
     * token 转换为认证信息
     *
     * @param token  token
     * @param logout 是否是登出请求 如果是登出请求 则不校验 token 是否过期
     * @return SecureUserAuthentication 认证信息
     */
    public SecureUserAuthentication authentication(String token, boolean logout) {
        DecodeToken decode = this.decode(TokenType.T, token);
        if (decode.isExpired() && !logout) {
            throw SecurityException.of(SecureCodes.TOKEN_EXPIRED);
        }
        SecureUser<?> secureUser = decode.getSecureUser();
        String tokenId = decode.getTokenId();
        return new SecureUserAuthentication(tokenId, secureUser);
    }


}

```

# com.medusa.gruul.common.security.resource.tool.jwt.JwtDeserializer

**文件路径**: `tool\jwt\JwtDeserializer.java`

## 代码文档
///
@author 张治保
@since 2023/11/28
 
///


## 文件结构
```
项目根目录
└── tool\jwt
    └── JwtDeserializer.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.resource.tool.jwt;

import io.jsonwebtoken.io.AbstractDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Reader;

/**
 * @author 张治保
 * @since 2023/11/28
 */
@Slf4j
public final class JwtDeserializer<T> extends AbstractDeserializer<T> {

    @Override
    protected T doDeserialize(Reader reader) throws IOException {
//        return (T) JSONB.parseObject(new byte[0], Map.class);
        throw new UnsupportedOperationException("Not implemented");

    }
}

```

# com.medusa.gruul.common.security.resource.tool.jwt.JwtSecurityContextRepository

**文件路径**: `tool\jwt\JwtSecurityContextRepository.java`

## 代码文档
///
SecurityContextRepository 用于加载和保存SecurityContext

@author 张治保
@since 2023/11/3
 
///

///
jwt 解码器
         
///

///
请求上下文
         
///

///
是否是登出请求
         
///

///
是否已生成
         
///

///
获取认证信息

@param token token
@return 认证信息
         
///


## 文件结构
```
项目根目录
└── tool\jwt
    └── JwtSecurityContextRepository.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.resource.tool.jwt;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.security.model.bean.CacheToken;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.bean.TokenKey;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.resource.exception.SecurityException;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.security.resource.model.SecureUserAuthentication;
import com.medusa.gruul.common.system.model.model.ClientType;
import io.vavr.control.Option;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

/**
 * SecurityContextRepository 用于加载和保存SecurityContext
 *
 * @author 张治保
 * @since 2023/11/3
 */
@Component
@RequiredArgsConstructor
public final class JwtSecurityContextRepository implements SecurityContextRepository {
    private static final Authentication ANONYMOUS = new AnonymousAuthenticationToken("key", "anonymous", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
    private static final RequestMatcher LOGOUT_REQUEST_MATCHER = new AntPathRequestMatcher("/logout", "POST");
    private static final String CONTEXT_KEY = JwtSecurityContextRepository.class.getName() + "_C-T-X";
    private final JwtDecoder jwtDecoder;

    @Override
    @SuppressWarnings("deprecation")
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        return loadDeferredContext(requestResponseHolder.getRequest()).get();
    }

    @Override
    public DeferredSecurityContext loadDeferredContext(HttpServletRequest request) {
        return new JwtDeferredSecurityContext(jwtDecoder, request, LOGOUT_REQUEST_MATCHER.matches(request));
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        if (context.getAuthentication() instanceof SecureUserAuthentication) {
            SecurityContextHolder.setContext(context);
        }
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return request.getAttribute(CONTEXT_KEY) != null;
    }

    @RequiredArgsConstructor
    public final static class JwtDeferredSecurityContext implements DeferredSecurityContext {

        /**
         * jwt 解码器
         */
        private final JwtDecoder jwtDecoder;
        /**
         * 请求上下文
         */
        private final HttpServletRequest request;
        /**
         * 是否是登出请求
         */
        private final boolean logout;

        /**
         * 是否已生成
         */
        private boolean generated;

        @Override
        public SecurityContext get() {
            Object attribute;
            if (generated && (attribute = request.getAttribute(CONTEXT_KEY)) != null) {
                return (SecurityContext) attribute;
            }
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            Authentication authentication = this.getAuthentication(request.getHeader(ISecurity.HEADER));
            context.setAuthentication(authentication);
            generated = true;
            request.setAttribute(CONTEXT_KEY, context);
            return context;
        }

        /**
         * 获取认证信息
         *
         * @param token token
         * @return 认证信息
         */
        private Authentication getAuthentication(String token) {
            if (StrUtil.isEmpty(token)) {
                return ANONYMOUS;
            }
            SecureUserAuthentication authentication = jwtDecoder.authentication(token, logout);
            if (logout) {
                return authentication;
            }
            SecureUser<?> secureUser = authentication.getUser();

            String tokenId = authentication.getTokenId();
            ClientType clientType = secureUser.getClientType();
            Long shopId = secureUser.getShopId();
            Long userId = secureUser.getId();
            //取出用户的 token 信息
            Option<CacheToken> cacheTokenOpt = ISecurity.getCacheToken(new TokenKey(clientType, shopId, userId, tokenId));
            //缓存的 token 为空 说明 所有 token 包含刷新 token 已过期
            if (cacheTokenOpt.isEmpty()) {
                throw SecurityException.of(SecureCodes.REFRESH_TOKEN_EXPIRED);
            }
            CacheToken cacheToken = cacheTokenOpt.get();
            return switch (cacheToken.getState()) {
                // 正常在线 说明 token 有效 直接返回
                case ONLINE -> authentication;
                // 重置用户资料 token标记为已过期 触发用户刷新用户资料
                case REFRESH -> throw SecurityException.of(SecureCodes.TOKEN_EXPIRED);
                // 被踢出 抛出账户过期异常
                case KICK -> {
                    //被踢出 跑出账户过期异常 提醒用户
                    //用户下线 删除 Token
                    ISecurity.offlineUser(clientType, shopId, userId, tokenId);
                    //抛出异常
                    throw SecurityException.of(SecureCodes.ACCOUNT_EXPIRED.msgEx(cacheToken.getMessage()));
                }
            };
        }

        @Override
        public boolean isGenerated() {
            return generated;
        }
    }
}

```

# com.medusa.gruul.common.security.resource.extension.dubbo.SecureUserAuthenticationCodec

**文件路径**: `extension\dubbo\SecureUserAuthenticationCodec.java`

## 代码文档
///
@author 张治保
@since 2023/11/7
 
///


## 文件结构
```
项目根目录
└── extension\dubbo
    └── SecureUserAuthenticationCodec.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.resource.extension.dubbo;

import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.medusa.gruul.common.security.resource.model.SecureUserAuthentication;
import org.apache.dubbo.spring.security.jackson.ObjectMapperCodec;
import org.apache.dubbo.spring.security.jackson.ObjectMapperCodecCustomer;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.jaas.JaasAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.io.IOException;

/**
 * @author 张治保
 * @since 2023/11/7
 */
public final class SecureUserAuthenticationCodec implements ObjectMapperCodecCustomer {
    @Override
    public void customize(ObjectMapperCodec objectMapperCodec) {
        objectMapperCodec.configureMapper(
                objectMapper -> objectMapper.addMixIn(Authentication.class, SecureUserAuthenticationMixIn.class)
        );
    }

    @JsonSerialize(using = SecureUserAuthenticationSerializer.class)
    @JsonDeserialize(using = SecureUserAuthenticationDeserializer.class)
    public abstract static class SecureUserAuthenticationMixIn {
    }

    public final static class SecureUserAuthenticationSerializer extends JsonSerializer<Authentication> {

        @Override
        public void serialize(Authentication value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeBinary(
                    JSONB.toBytes(value, JSONWriter.Feature.WriteClassName,
                            JSONWriter.Feature.FieldBased,
                            JSONWriter.Feature.ErrorOnNoneSerializable,
                            JSONWriter.Feature.ReferenceDetection,
                            JSONWriter.Feature.WriteNulls,
                            JSONWriter.Feature.NotWriteDefaultValue,
                            JSONWriter.Feature.NotWriteHashMapArrayListClassName,
                            JSONWriter.Feature.WriteNameAsSymbol)
            );
        }

        @Override
        public void serializeWithType(Authentication value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
            this.serialize(value, gen, serializers);
        }
    }

    public final static class SecureUserAuthenticationDeserializer extends JsonDeserializer<Authentication> {
        @Override
        public Authentication deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
            return JSONB.parseObject(
                    parser.getBinaryValue(), Authentication.class,
                    JSONReader.autoTypeFilter(
                            SecureUserAuthentication.class,
                            AnonymousAuthenticationToken.class,
                            JaasAuthenticationToken.class,
                            PreAuthenticatedAuthenticationToken.class,
                            RememberMeAuthenticationToken.class,
                            UsernamePasswordAuthenticationToken.class,
                            TestingAuthenticationToken.class
                    ),
                    JSONReader.Feature.UseDefaultConstructorAsPossible,
                    JSONReader.Feature.ErrorOnNoneSerializable,
                    JSONReader.Feature.IgnoreAutoTypeNotMatch,
                    JSONReader.Feature.UseNativeObject,
                    JSONReader.Feature.FieldBased);
        }

        @Override
        public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
            return this.deserialize(p, ctxt);
        }

        @Override
        public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer, Authentication intoValue) throws IOException {
            return this.deserialize(p, ctxt);
        }
    }


}

```


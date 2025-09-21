# com.medusa.gruul.common.security.server.LoginAuthenticationFilter

**文件路径**: `security\server\LoginAuthenticationFilter.java`

## 代码文档
///
登录认证过滤器 用于处理登录逻辑 参考 {@link org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter}

@author 张治保
@since 2023/11/2
 
///

///
根据 grantType 获取对应的 AuthenticationRequest类型

@param grantType grantType
@return AuthenticationRequest 类型
     
///


## 文件结构
```
项目根目录
└── security\server
    └── LoginAuthenticationFilter.java
```

## 完整代码
```java
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

```

# com.medusa.gruul.common.security.server.SecurityServerAutoconfigure

**文件路径**: `security\server\SecurityServerAutoconfigure.java`

## 代码文档
///
security server 自动装配

@author 张治保
@since 2023/11/2
 
///


## 文件结构
```
项目根目录
└── security\server
    └── SecurityServerAutoconfigure.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.server;

import com.medusa.gruul.common.security.resource.properties.SecurityClientProperties;
import com.medusa.gruul.common.security.resource.tool.SecurityChainConfig;
import com.medusa.gruul.common.security.server.handler.LogoutHandler;
import com.medusa.gruul.common.security.server.model.AuthenticationRequest;
import com.medusa.gruul.common.security.server.provider.IAuthenticationProvider;
import com.medusa.gruul.common.security.server.provider.IReloadUserProvider;
import com.medusa.gruul.common.security.server.provider.RefreshTokenAuthenticationProvider;
import com.medusa.gruul.common.security.server.tool.JwtEncoder;
import com.medusa.gruul.common.security.server.tool.ServerJwtSecretKey;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.List;

/**
 * security server 自动装配
 *
 * @author 张治保
 * @since 2023/11/2
 */
@Import({JwtEncoder.class, RefreshTokenAuthenticationProvider.class, IReloadUserProvider.DefaultReloadUserProvider.class})
@EnableConfigurationProperties(SecurityServerProperties.class)
public class SecurityServerAutoconfigure {

    @Bean
    public ServerJwtSecretKey serverJwtSecretKey(SecurityServerProperties serverProperties, SecurityClientProperties clientProperties) {
        return new ServerJwtSecretKey(serverProperties.getPrivateKey(), clientProperties.getPublicKey(), clientProperties.getPayloadKey());
    }

    @Bean
    @SneakyThrows
    public SecurityChainConfig securityChainConfig(
            List<IAuthenticationProvider<? extends AuthenticationRequest>> providers,
            RequestMappingHandlerAdapter adapter,
            JwtEncoder encoder,
            SecurityServerProperties securityServerProperties
    ) {
        return http -> {
            //登录逻辑
            http.addFilterBefore(
                    new LoginAuthenticationFilter(
                            //是否允许重复登录
                            securityServerProperties.isRepeatLogin(),
                            //认证提供器
                            providers,
                            //jwt编码器
                            encoder,
                            //消息转换器
                            adapter.getMessageConverters()
                    ),
                    UsernamePasswordAuthenticationFilter.class
            );
            //登出逻辑
            try {
                http.logout(logout -> logout.addLogoutHandler(new LogoutHandler()).permitAll());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}

```

# com.medusa.gruul.common.security.server.SecurityServerProperties

**文件路径**: `security\server\SecurityServerProperties.java`

## 代码文档
///
security server 配置

@author 张治保
@since 2023/11/2
 
///

///
是否允许重复登录 默认不允许 同一种客户端
     
///

///
jwt 私钥
     
///

///
token 过期时间
     
///

///
refresh token 过期时间
     
///


## 文件结构
```
项目根目录
└── security\server
    └── SecurityServerProperties.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.server;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * security server 配置
 *
 * @author 张治保
 * @since 2023/11/2
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.security.server")
public final class SecurityServerProperties {

    /**
     * 是否允许重复登录 默认不允许 同一种客户端
     */
    private boolean repeatLogin = false;

    /**
     * jwt 私钥
     */
    private String privateKey;

    /**
     * token 过期时间
     */
    private Duration tokenExpired = Duration.ofHours(2);

    /**
     * refresh token 过期时间
     */
    private Duration refreshTokenExpired = Duration.ofDays(7);
}

```

# com.medusa.gruul.common.security.server.annotation.GrantType

**文件路径**: `server\annotation\GrantType.java`

## 代码文档
///
登录认证类型

@author 张治保
@since 2023/11/2
 
///


## 文件结构
```
项目根目录
└── server\annotation
    └── GrantType.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.server.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 登录认证类型
 *
 * @author 张治保
 * @since 2023/11/2
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Inherited
public @interface GrantType {

    String PARAMETER_NAME = "grant_type";

    String value();
}

```

# com.medusa.gruul.common.security.server.handler.FailureHandler

**文件路径**: `server\handler\FailureHandler.java`

## 代码文档
///
@author 张治保
@since 2023/11/2
 
///


## 文件结构
```
项目根目录
└── server\handler
    └── FailureHandler.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.server.handler;

import com.medusa.gruul.common.security.resource.exception.SecureExceptionAdvice;
import com.medusa.gruul.common.security.resource.tool.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

/**
 * @author 张治保
 * @since 2023/11/2
 */
public final class FailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        ResponseUtil.response(
                response,
                SecureExceptionAdvice.authExceptionMapping(exception)
        );
    }
}

```

# com.medusa.gruul.common.security.server.handler.LogoutHandler

**文件路径**: `server\handler\LogoutHandler.java`

## 代码文档
///
登出逻辑 清楚缓存数据

@author 张治保
@since 2023/11/4
 
///


## 文件结构
```
项目根目录
└── server\handler
    └── LogoutHandler.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.server.handler;

import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.security.resource.model.SecureUserAuthentication;
import com.medusa.gruul.common.security.resource.tool.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;

/**
 * 登出逻辑 清楚缓存数据
 *
 * @author 张治保
 * @since 2023/11/4
 */
public final class LogoutHandler implements org.springframework.security.web.authentication.logout.LogoutHandler {

    @Override
    @SneakyThrows
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Result<Object> resp = Result.ok();
        if (!(authentication instanceof SecureUserAuthentication secureUserAuthentication)) {
            ResponseUtil.response(response, resp);
            return;
        }
        SecureUser<?> user = secureUserAuthentication.user();
        //用户下线
        ISecurity.offlineUser(user.getClientType(), user.getShopId(), user.getId(), secureUserAuthentication.getTokenId());
        //
        ResponseUtil.response(response, resp);
    }
}

```

# com.medusa.gruul.common.security.server.handler.SuccessHandler

**文件路径**: `server\handler\SuccessHandler.java`

## 代码文档
///
@author 张治保
@since 2023/11/2
 
///


## 文件结构
```
项目根目录
└── server\handler
    └── SuccessHandler.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.server.handler;

import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.security.resource.model.OnlineUserParam;
import com.medusa.gruul.common.security.resource.model.SecureUserAuthentication;
import com.medusa.gruul.common.security.resource.tool.ResponseUtil;
import com.medusa.gruul.common.security.server.model.AuthenticationResp;
import com.medusa.gruul.common.security.server.tool.JwtEncoder;
import com.medusa.gruul.common.system.model.model.ClientType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Set;

/**
 * @author 张治保
 * @since 2023/11/2
 */
@RequiredArgsConstructor
public final class SuccessHandler implements AuthenticationSuccessHandler {


    private final boolean repeatLogin;
    private final JwtEncoder jwtEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        SecureUserAuthentication secureUserAuthentication = (SecureUserAuthentication) authentication;
        SecureUser<?> user = secureUserAuthentication.user();
        ClientType clientType = user.getClientType();
        Long shopId = user.getShopId();
        Long userId = user.getId();
        if (clientType == null || shopId == null || userId == null) {
            ResponseUtil.response(response, Result.failed(SecureCodes.ACCOUNT_INVALID));
            return;
        }
        AuthenticationResp token = jwtEncoder.encode(secureUserAuthentication);
        if (!repeatLogin) {
            //用户下线
            ISecurity.kickUsers(clientType, shopId, Set.of(userId), SecureCodes.TOKEN_CHANGED.msg());
        }
        //用户上线
        ISecurity.onlineUser(
                new OnlineUserParam(
                        clientType,
                        shopId,
                        userId,
                        secureUserAuthentication.tokenId(),
                        token.getRefreshToken().getExpireAt()
                )
        );
        //返回token
        ResponseUtil.response(response, Result.ok(token));
    }
}

```

# com.medusa.gruul.common.security.server.model.AuthenticationRequest

**文件路径**: `server\model\AuthenticationRequest.java`

## 代码文档
///
登录认证请求参数

@author 张治保
@since 2023/11/2
 
///


## 文件结构
```
项目根目录
└── server\model
    └── AuthenticationRequest.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.server.model;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

/**
 * 登录认证请求参数
 *
 * @author 张治保
 * @since 2023/11/2
 */
@Getter
public class AuthenticationRequest implements Authentication {

    private final String tokenId = IdUtil.fastSimpleUUID();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of();
    }

    @Override
    public Object getCredentials() {
        return StrUtil.EMPTY;
    }

    @Override
    public Object getDetails() {
        return StrUtil.EMPTY;
    }

    @Override
    public Object getPrincipal() {
        return StrUtil.EMPTY;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    }

    @Override
    public String getName() {
        return StrUtil.EMPTY;
    }

}

```

# com.medusa.gruul.common.security.server.model.AuthenticationResp

**文件路径**: `server\model\AuthenticationResp.java`

## 代码文档
///
响应数据

@author 张治保
@since 2023/11/2
 
///

///
刷新token
     
///

///
额外信息
     
///


## 文件结构
```
项目根目录
└── server\model
    └── AuthenticationResp.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.server.model;

import com.medusa.gruul.common.security.model.bean.EncodeToken;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 响应数据
 *
 * @author 张治保
 * @since 2023/11/2
 */
@Getter
@Setter
@Accessors(chain = true)
public final class AuthenticationResp extends EncodeToken {

    /**
     * 刷新token
     */
    private EncodeToken refreshToken;

    /**
     * 额外信息
     */
    private Object open = Map.of();
}

```

# com.medusa.gruul.common.security.server.model.RefreshTokenRequest

**文件路径**: `server\model\RefreshTokenRequest.java`

## 代码文档
///
刷新 token登录认证请求参数

@author 张治保
@since 2023/11/2
 
///

///
刷新token
     
///


## 文件结构
```
项目根目录
└── server\model
    └── RefreshTokenRequest.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.server.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 刷新 token登录认证请求参数
 *
 * @author 张治保
 * @since 2023/11/2
 */
@Getter
@Setter
@Accessors(chain = true)
public final class RefreshTokenRequest extends AuthenticationRequest {
    /**
     * 刷新token
     */
    private String value;
}
```

# com.medusa.gruul.common.security.server.provider.IAuthenticationProvider

**文件路径**: `server\provider\IAuthenticationProvider.java`

## 代码文档
///
认证处理器接口 用于处理具体的认证类型的认证逻辑

@author 张治保
@since 2023/11/2
 
///

///
设置开放数据

@param secureUser 安全用户
     
///

///
处理认证请求并返回认证结果

@param request 认证请求参数
@return 认证后的数据
@throws AuthenticationException if authentication fails.
     
///

///
处理认证逻辑

@param authentication 认证请求参数
@return 认证后的用户数据
@throws AuthenticationException 认证失败的异常
     
///

///
判断是否支持该类型的认证请求

@param authentication 认证参数请求类型
@return 是否支持改类型请求
     
///

///
获取认证请求类型

@return 认证请求类型
     
///


## 文件结构
```
项目根目录
└── server\provider
    └── IAuthenticationProvider.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.server.provider;

import cn.hutool.core.util.TypeUtil;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.model.SecureUserAuthentication;
import com.medusa.gruul.common.security.server.model.AuthenticationRequest;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * 认证处理器接口 用于处理具体的认证类型的认证逻辑
 *
 * @author 张治保
 * @since 2023/11/2
 */
public interface IAuthenticationProvider<T extends AuthenticationRequest> extends AuthenticationProvider {


    Map<Class<? extends IAuthenticationProvider<?>>, Class<? extends AuthenticationRequest>> REQUEST_TYPE_MAP = new HashMap<>();

    /**
     * 设置开放数据
     *
     * @param secureUser 安全用户
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void setOpenData(SecureUser<?> secureUser) {
        OpenDataResolver resolver = OpenDataHandler.OPEN_DATA_RESOLVER;
        if (resolver == null) {
            return;
        }
        secureUser.setOpen(resolver.resolve(secureUser));
    }

    /**
     * 处理认证请求并返回认证结果
     *
     * @param request 认证请求参数
     * @return 认证后的数据
     * @throws AuthenticationException if authentication fails.
     */
    @Override
    @SuppressWarnings("unchecked")
    default Authentication authenticate(Authentication request) throws AuthenticationException {
        SecureUser<?> secureUser = authenticate((T) request);
        setOpenData(secureUser);
        return SecureUserAuthentication.of(((AuthenticationRequest) request).getTokenId(), secureUser);
    }

    /**
     * 处理认证逻辑
     *
     * @param authentication 认证请求参数
     * @return 认证后的用户数据
     * @throws AuthenticationException 认证失败的异常
     */
    SecureUser<?> authenticate(T authentication) throws AuthenticationException;

    /**
     * 判断是否支持该类型的认证请求
     *
     * @param authentication 认证参数请求类型
     * @return 是否支持改类型请求
     */
    @Override
    default boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(requestType());
    }

    /**
     * 获取认证请求类型
     *
     * @return 认证请求类型
     */
    @SuppressWarnings("unchecked")
    default Class<T> requestType() {
        Class<? extends AuthenticationRequest> clazz = REQUEST_TYPE_MAP.computeIfAbsent(
                (Class<? extends IAuthenticationProvider<?>>) this.getClass(),
                k -> (Class<T>) TypeUtil.getTypeArgument(this.getClass(), 0)
        );
        return (Class<T>) clazz;
    }


    @SuppressWarnings("rawtypes")
    class OpenDataHandler {
        private static final OpenDataResolver OPEN_DATA_RESOLVER;

        static {
            ServiceLoader<OpenDataResolver> load = ServiceLoader.load(OpenDataResolver.class);
            OPEN_DATA_RESOLVER = load.findFirst().orElse(null);
        }
    }
}

```

# com.medusa.gruul.common.security.server.provider.IReloadUserProvider

**文件路径**: `server\provider\IReloadUserProvider.java`

## 代码文档
///
重置用户 TOKEN 提供器

@author 张治保
@since 2023/11/16
 
///

///
根据用户id加载用户信息

@param preUser 加载之前的用户认证资料
@return 用户信息
     
///


## 文件结构
```
项目根目录
└── server\provider
    └── IReloadUserProvider.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.server.provider;

import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.resource.exception.SecurityException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

/**
 * 重置用户 TOKEN 提供器
 *
 * @author 张治保
 * @since 2023/11/16
 */
public interface IReloadUserProvider {

    /**
     * 根据用户id加载用户信息
     *
     * @param preUser 加载之前的用户认证资料
     * @return 用户信息
     */
    SecureUser<?> loadUser(SecureUser<?> preUser);

    @ConditionalOnMissingBean(IReloadUserProvider.class)
    class DefaultReloadUserProvider implements IReloadUserProvider {

        @Override
        public SecureUser<?> loadUser(SecureUser<?> preUser) {
            throw SecurityException.of(SecureCodes.REFRESH_TOKEN_EXPIRED);
        }

    }
}

```

# com.medusa.gruul.common.security.server.provider.OpenDataResolver

**文件路径**: `server\provider\OpenDataResolver.java`

## 代码文档
///
认证成功后响应的的开放数据解析器

@author 张治保
@since 2024/6/14
 
///

///
渲染开放数据

@param secureUser 安全用户
@return 开放数据
     
///


## 文件结构
```
项目根目录
└── server\provider
    └── OpenDataResolver.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.server.provider;

import com.medusa.gruul.common.security.model.bean.SecureUser;

import java.util.Map;

/**
 * 认证成功后响应的的开放数据解析器
 *
 * @author 张治保
 * @since 2024/6/14
 */
public interface OpenDataResolver<T> {

    /**
     * 渲染开放数据
     *
     * @param secureUser 安全用户
     * @return 开放数据
     */
    Map<String, Object> resolve(SecureUser<T> secureUser);

}

```

# com.medusa.gruul.common.security.server.provider.RefreshTokenAuthenticationProvider

**文件路径**: `server\provider\RefreshTokenAuthenticationProvider.java`

## 代码文档
///
@author 张治保
@since 2023/11/3
 
///


## 文件结构
```
项目根目录
└── server\provider
    └── RefreshTokenAuthenticationProvider.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.server.provider;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.security.model.bean.CacheToken;
import com.medusa.gruul.common.security.model.bean.DecodeToken;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.bean.TokenKey;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.model.enums.TokenState;
import com.medusa.gruul.common.security.model.enums.TokenType;
import com.medusa.gruul.common.security.resource.exception.SecurityException;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.security.resource.tool.jwt.JwtDecoder;
import com.medusa.gruul.common.security.server.annotation.GrantType;
import com.medusa.gruul.common.security.server.model.RefreshTokenRequest;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * @author 张治保
 * @since 2023/11/3
 */
@Component
@GrantType("refresh_token")
@RequiredArgsConstructor
public final class RefreshTokenAuthenticationProvider implements IAuthenticationProvider<RefreshTokenRequest> {

    private final JwtDecoder jwtDecoder;
    private final IReloadUserProvider refreshTokenUserProvider;

    @Override
    public SecureUser<?> authenticate(RefreshTokenRequest authentication) throws AuthenticationException {
        String token = authentication.getValue();
        if (StrUtil.isEmpty(token)) {
            throw SecurityException.of(SecureCodes.REQUEST_INVALID);
        }
        DecodeToken decode = jwtDecoder.decode(TokenType.RT, token);
        if (decode.isExpired()) {
            throw SecurityException.of(SecureCodes.REFRESH_TOKEN_EXPIRED);
        }
        SecureUser<?> secureUser = decode.getSecureUser();
        //比较 token Id 判断当前 refreshToken是否仍然可用
        String tokenId = decode.getTokenId();
        TokenKey tokenKey = new TokenKey(secureUser.getClientType(), secureUser.getShopId(), secureUser.getId(), tokenId);
        Option<CacheToken> cacheTokenOpt = ISecurity.getCacheToken(tokenKey);
        //如果缓存的数据 说明 1. token 已失效 2. token 已被踢出
        //根据用户 id 重新 load 客户数据
        if (cacheTokenOpt.isEmpty()) {
            throw SecurityException.of(SecureCodes.REFRESH_TOKEN_EXPIRED);
        }
        CacheToken cacheToken = cacheTokenOpt.get();
        TokenState state = cacheToken.getState();
        try {
            return switch (state) {
                //抛出异常 提示
                case KICK -> throw SecurityException.of(SecureCodes.ACCOUNT_EXPIRED.msgEx(cacheToken.getMessage()));
                //刷新用户资料 重新生成 新的 Token
                case REFRESH -> refreshTokenUserProvider.loadUser(secureUser);
                //使用原有用户资料 重新生成 新的 Token
                default -> secureUser;
            };
        } finally {
            //使原有token失效
            ISecurity.offlineUser(tokenKey);
        }
    }

    @Override
    public Class<RefreshTokenRequest> requestType() {
        return RefreshTokenRequest.class;
    }
}

```

# com.medusa.gruul.common.security.server.tool.JwtEncoder

**文件路径**: `server\tool\JwtEncoder.java`

## 代码文档
///
token 编码器

@author 张治保
@since 2023/11/2
 
///

///
token 过期时间
     
///

///
refresh token 过期时间
     
///

///
jwt 密钥数据
     
///

///
jwt 反序列化器
     
///

///
编码 token

@param authentication 认证信息
@return AuthenticationResp 编码后的token 信息
     
///

///
编码 token

@param baseTime   基准时间
@param type       token 类型
@param tokenId    token id
@param user       用户信息
@param expireTime 过期时间
@return EncodeToken 编码后的token 信息
     
///


## 文件结构
```
项目根目录
└── server\tool
    └── JwtEncoder.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.server.tool;

import cn.hutool.core.date.DateUtil;
import com.medusa.gruul.common.security.model.bean.EncodeToken;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.TokenType;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.security.resource.model.SecureUserAuthentication;
import com.medusa.gruul.common.security.server.SecurityServerProperties;
import com.medusa.gruul.common.security.server.model.AuthenticationResp;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Serializer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * token 编码器
 *
 * @author 张治保
 * @since 2023/11/2
 */
public class JwtEncoder {

    /**
     * token 过期时间
     */
    private final Duration tokenExpired;
    /**
     * refresh token 过期时间
     */
    private final Duration refreshTokenExpired;
    /**
     * jwt 密钥数据
     */
    private final ServerJwtSecretKey serverJwtSecretKey;
    /**
     * jwt 反序列化器
     */
    private final Serializer<Map<String, ?>> jwtDeserializer = new JwtSerializer<>();


    public JwtEncoder(SecurityServerProperties securityServerProperties, ServerJwtSecretKey serverJwtSecretKey) {
        this.tokenExpired = securityServerProperties.getTokenExpired();
        this.refreshTokenExpired = securityServerProperties.getRefreshTokenExpired();
        this.serverJwtSecretKey = serverJwtSecretKey;
    }

    /**
     * 编码 token
     *
     * @param authentication 认证信息
     * @return AuthenticationResp 编码后的token 信息
     */
    public AuthenticationResp encode(SecureUserAuthentication authentication) {
        String tokenId = authentication.tokenId();
        SecureUser<?> user = authentication.user();
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> open = user.open();
        EncodeToken token = encodeToken(now, TokenType.T, tokenId, user, tokenExpired);
        EncodeToken refreshToken = encodeToken(now, TokenType.RT, tokenId, user, refreshTokenExpired);
        AuthenticationResp authenticationToken = new AuthenticationResp();
        authenticationToken
                .setValue(token.getValue())
                .setExpireAt(token.getExpireAt())
                .setExpiresIn(token.getExpiresIn());
        return authenticationToken.setRefreshToken(refreshToken)
                .setOpen(open);

    }

    /**
     * 编码 token
     *
     * @param baseTime   基准时间
     * @param type       token 类型
     * @param tokenId    token id
     * @param user       用户信息
     * @param expireTime 过期时间
     * @return EncodeToken 编码后的token 信息
     */
    private EncodeToken encodeToken(LocalDateTime baseTime, TokenType type, String tokenId, SecureUser<?> user, Duration expireTime) {
        LocalDateTime expireAt = baseTime.plus(expireTime);
        String compact = Jwts.builder()
                .compressWith(Jwts.ZIP.GZIP)
                .json(jwtDeserializer)
                .signWith(serverJwtSecretKey.signKey())
                .id(tokenId)
                .claim(TokenType.TYPE, type.name())
                .claim(TokenType.KEY, serverJwtSecretKey.payload().encrypt(ISecurity.toPrincipal(user)))
                .issuedAt(DateUtil.date(baseTime))
                .expiration(DateUtil.date(expireAt))
                .compact();
        return new EncodeToken()
                .setValue(compact)
                .setExpireAt(expireAt)
                .setExpiresIn(expireTime.getSeconds());
    }

}

```

# com.medusa.gruul.common.security.server.tool.JwtSerializer

**文件路径**: `server\tool\JwtSerializer.java`

## 代码文档
///
jwt 序列化器

@author 张治保
@since 2023/11/28
 
///


## 文件结构
```
项目根目录
└── server\tool
    └── JwtSerializer.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.server.tool;

import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.JSONWriter;
import io.jsonwebtoken.io.AbstractSerializer;

import java.io.OutputStream;

/**
 * jwt 序列化器
 *
 * @author 张治保
 * @since 2023/11/28
 */
public class JwtSerializer<T> extends AbstractSerializer<T> {
    @Override
    protected void doSerialize(T data, OutputStream out) throws Exception {
        try (out) {
            JSONB.writeTo(out, data, JSONWriter.Feature.BeanToArray, JSONWriter.Feature.WriteByteArrayAsBase64);
        }
    }
}

```

# com.medusa.gruul.common.security.server.tool.ServerJwtSecretKey

**文件路径**: `server\tool\ServerJwtSecretKey.java`

## 代码文档
///
@author 张治保
@since 2023/11/30
 
///


## 文件结构
```
项目根目录
└── server\tool
    └── ServerJwtSecretKey.java
```

## 完整代码
```java
package com.medusa.gruul.common.security.server.tool;

import cn.hutool.crypto.asymmetric.ECIES;
import cn.hutool.crypto.symmetric.AES;
import com.medusa.gruul.common.security.resource.tool.jwt.IClientSecretKey;
import com.medusa.gruul.global.model.helper.SecureHelper;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author 张治保
 * @since 2023/11/30
 */
public class ServerJwtSecretKey implements IClientSecretKey {
    private final ECIES ecies;
    private final AES payloadSecret;

    public ServerJwtSecretKey(String privateKey, String publicKey, String payloadKey) {
        this.ecies = new ECIES(privateKey, publicKey);
        this.payloadSecret = new AES(SecureHelper.aesKey(payloadKey));
    }

    public PrivateKey signKey() {
        return ecies.getPrivateKey();
    }

    @Override
    public PublicKey verifyKey() {
        return ecies.getPublicKey();
    }

    @Override
    public AES payload() {
        return payloadSecret;
    }
}

```


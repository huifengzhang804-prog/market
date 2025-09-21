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

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

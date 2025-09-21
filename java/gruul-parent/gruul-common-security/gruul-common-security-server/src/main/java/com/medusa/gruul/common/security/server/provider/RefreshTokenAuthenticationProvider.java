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

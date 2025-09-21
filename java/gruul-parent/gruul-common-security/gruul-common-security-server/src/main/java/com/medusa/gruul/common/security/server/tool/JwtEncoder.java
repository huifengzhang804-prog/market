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

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

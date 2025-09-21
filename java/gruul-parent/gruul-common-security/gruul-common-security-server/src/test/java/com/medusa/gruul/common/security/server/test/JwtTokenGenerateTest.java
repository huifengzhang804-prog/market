package com.medusa.gruul.common.security.server.test;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.asymmetric.ECIES;
import com.medusa.gruul.common.security.model.enums.TokenType;
import com.medusa.gruul.common.security.server.tool.JwtSerializer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.CustomJwtParser;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * @author 张治保
 * @since 2024/12/5
 */
public class JwtTokenGenerateTest {

    private static final String PUBLIC_KEY = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEe8rySquJsE2B16Tk7IojsaTHeRK7wXASjhvZdtGTVFgKXPuC0S22VLrpihkrC0bLc0RN2Wup2sKWzSw6TiICfA==";
    private static final String PRIVATE_KEY = "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCBFqJmRJlgoAZ0swwWBFNEoZ+d5lKl5KyjTDOqnFh0+ow==";
    private static final String TOKEN_ID = IdUtil.fastSimpleUUID();
    private static final ECIES ecies = new ECIES(PRIVATE_KEY, PUBLIC_KEY);
    private static final LocalDateTime issuedAt = LocalDateTime.now();
    private static final LocalDateTime expiration = issuedAt.plusDays(1);
    
    private static final String KEY = RandomUtil.randomString(4000);

    @Test
    public void test() {
        Claims claims = encDec(false);
        Claims claims1 = encDec(true);
        Assert.assertEquals(claims, claims1);
    }

    private Claims encDec(boolean compress) {
        String token = token(compress);
        System.out.println("------------------" + (compress ? "压缩" : "未压缩") + "------------------");
        System.out.println(token);
        System.out.println("值  ：" + token);
        System.out.println("长度：" + token.length());
        Claims claims = decode(token);
        System.out.println("负载：" + claims);
        return claims;
    }

    private String token(boolean compress) {
        JwtBuilder builder = Jwts.builder()
                .json(new JwtSerializer<>())
                .signWith(ecies.getPrivateKey())
                .id(TOKEN_ID)
                .claim(TokenType.TYPE, TokenType.T.name())
                .claim(TokenType.KEY, KEY)
                .issuedAt(DateUtil.date(issuedAt))
                .expiration(DateUtil.date(expiration));
        if (compress) {
            builder.compressWith(Jwts.ZIP.GZIP);
        }
        return builder.compact();
    }

    private Claims decode(String token) {
        CustomJwtParser parser = new CustomJwtParser(ecies.getPublicKey());
        return (Claims) parser.parse(token).getPayload();
    }
}

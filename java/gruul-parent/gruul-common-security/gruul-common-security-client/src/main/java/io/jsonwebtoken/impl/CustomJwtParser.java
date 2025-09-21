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

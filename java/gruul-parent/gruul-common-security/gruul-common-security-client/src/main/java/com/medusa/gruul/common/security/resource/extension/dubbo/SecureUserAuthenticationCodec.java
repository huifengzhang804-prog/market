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

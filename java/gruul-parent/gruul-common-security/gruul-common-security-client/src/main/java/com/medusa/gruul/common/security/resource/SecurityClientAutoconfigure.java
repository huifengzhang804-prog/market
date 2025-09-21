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

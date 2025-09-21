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

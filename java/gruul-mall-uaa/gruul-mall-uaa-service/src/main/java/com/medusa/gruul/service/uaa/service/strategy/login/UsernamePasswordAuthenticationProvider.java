package com.medusa.gruul.service.uaa.service.strategy.login;

import com.medusa.gruul.common.encrypt.Crypt;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.resource.exception.SecurityException;
import com.medusa.gruul.common.security.server.annotation.GrantType;
import com.medusa.gruul.common.security.server.model.AuthenticationRequest;
import com.medusa.gruul.common.security.server.provider.IAuthenticationProvider;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.service.uaa.service.mp.entity.User;
import com.medusa.gruul.service.uaa.service.mp.service.IUserService;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author 张治保
 * @since 2024/4/19
 */
@Component
@GrantType("password")
@RequiredArgsConstructor
public class UsernamePasswordAuthenticationProvider implements IAuthenticationProvider<UsernamePasswordAuthenticationProvider.UsernamePassword> {

    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ReloadUserProvider reloadUserProvider;

    @Override
    public SecureUser<?> authenticate(UsernamePassword authentication) throws AuthenticationException {
        String username = authentication.getUsername();
        User user = userService.lambdaQuery()
                .eq(User::getUsername, username)
                .or()
                .eq(User::getMobile, username)
                .one();
        String decryptPassword = authentication.getPassword();
        //校验账号密码
        if (user == null || !passwordEncoder.matches(decryptPassword, user.getPassword())) {
            throw SecurityException.of(SecureCodes.USERNAME_PASSWORD_INVALID);
        }
        return reloadUserProvider.loadUser(ISystem.clientTypeMust(), ISystem.shopIdOpt().getOrNull(), user);
    }

    @Override
    public Class<UsernamePassword> requestType() {
        return UsernamePassword.class;
    }

    @Getter
    @Setter
    @ToString
    public static class UsernamePassword extends AuthenticationRequest {
        /**
         * 用户名
         */
        @NotBlank
        @Crypt
        private String username;

        /**
         * 密码
         */
        @NotBlank
        @Crypt
        private String password;

    }
}

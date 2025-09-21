package com.medusa.gruul.service.uaa.service.strategy.login;

import cn.hutool.core.lang.RegexPool;
import com.medusa.gruul.common.encrypt.Crypt;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.server.annotation.GrantType;
import com.medusa.gruul.common.security.server.model.AuthenticationRequest;
import com.medusa.gruul.common.security.server.provider.IAuthenticationProvider;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.service.uaa.api.constant.UaaConstant;
import com.medusa.gruul.service.uaa.api.enums.SmsType;
import com.medusa.gruul.service.uaa.service.model.enums.UaaError;
import com.medusa.gruul.service.uaa.service.mp.entity.User;
import com.medusa.gruul.service.uaa.service.mp.service.IUserService;
import com.medusa.gruul.service.uaa.service.service.SmsService;
import com.medusa.gruul.service.uaa.service.service.UserDataHandlerService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * @author 张治保
 * @since 2024/4/20
 */
@Component
@GrantType("sms_code")
@RequiredArgsConstructor
public class SmsCodeAuthenticationProvider implements IAuthenticationProvider<SmsCodeAuthenticationProvider.SmsCode> {

    private final SmsService smsService;
    private final IUserService userService;
    private final UserDataHandlerService userDataHandlerService;
    private final ReloadUserProvider reloadUserProvider;

    @Override
    public SecureUser<?> authenticate(SmsCode authentication) throws AuthenticationException {
        /* 校验验证码
         */
        String mobile = authentication.getMobile();
        smsService.checkSmsCode(SmsType.LOGIN, mobile, authentication.getCode());
        //查询用户
        User user = userService.lambdaQuery().eq(User::getMobile, mobile).one();
        ClientType clientType = ISystem.clientTypeMust();
        if (Objects.isNull(user) && ClientType.SHOP_CONSOLE == clientType) {
           throw UaaError.MERCHANDISER_NOT_EXIST.exception();
        }
        //如果是消费者用户，且用户不存在则生成用户
        if (user == null && ClientType.CONSUMER == clientType) {
            user = userDataHandlerService.generateUser(mobile, null);
        }
        return reloadUserProvider.loadUser(clientType, ISystem.shopIdOpt().getOrNull(), user);
    }

    @Override
    public Class<SmsCode> requestType() {
        return SmsCode.class;
    }

    @Getter
    @Setter
    @ToString
    public static class SmsCode extends AuthenticationRequest {
        /**
         * 手机号
         */
        @NotBlank
        @Crypt
        @Pattern(regexp = RegexPool.MOBILE)
        private String mobile;

        /**
         * 验证码
         */
        @NotBlank
        @Crypt
        @Size(min = UaaConstant.SMS_CODE_LENGTH, max = UaaConstant.SMS_CODE_LENGTH)
        @Pattern(regexp = RegexPool.NUMBERS)
        private String code;
    }
}

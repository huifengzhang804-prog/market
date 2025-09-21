package com.medusa.gruul.service.uaa.service.strategy.login;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.encrypt.Crypt;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.server.annotation.GrantType;
import com.medusa.gruul.common.security.server.model.AuthenticationRequest;
import com.medusa.gruul.common.security.server.provider.IAuthenticationProvider;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.global.model.helper.CompletableTask;
import com.medusa.gruul.global.model.o.Final;
import com.medusa.gruul.service.uaa.service.mp.entity.User;
import com.medusa.gruul.service.uaa.service.mp.service.IUserService;
import com.medusa.gruul.service.uaa.service.service.UserDataHandlerService;
import com.medusa.gruul.service.uaa.service.service.WechatService;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

/**
 * @author 张治保
 * @since 2024/4/20
 */
@Component
@GrantType("wechat_code")
@RequiredArgsConstructor
public class WechatCodeAuthenticationProvider implements IAuthenticationProvider<WechatCodeAuthenticationProvider.WechatCode> {

    private final Executor uaaExecutor;
    private final IUserService userService;
    private final WechatService wechatService;
    private final UserDataHandlerService userDataHandlerService;
    private final ReloadUserProvider reloadUserProvider;

    @Override
    public SecureUser<?> authenticate(WechatCode authentication) throws AuthenticationException {

        Final<String> mobileRef = new Final<>();
        Final<String> openidRef = new Final<>();
        CompletableTask.getOrThrowException(
                CompletableTask.allOf(
                        uaaExecutor,
                        //手机号
                        () -> {
                            WxMaPhoneNumberInfo phoneInfo = wechatService.getPhoneInfo(authentication.getMobileCode());
                            mobileRef.set(phoneInfo.getPurePhoneNumber());
                        },
                        //openid
                        () -> {
                            WxMaJscode2SessionResult sessionResult = wechatService.getSession(authentication.getLoginCode());
                            openidRef.set(sessionResult.getOpenid());
                        }
                )
        );
        String mobile = mobileRef.get();
        String openid = openidRef.get();
        User user = userService.lambdaQuery().eq(User::getMobile, mobile).one();
        //未注册 且消费者端登陆
        if (user == null && ClientType.CONSUMER == ISystem.clientTypeMust()) {
            user = userDataHandlerService.generateUser(mobile, openid);
        }
        //openid不相等 则 更新 openid
        if (user != null && !StrUtil.equals(openid, user.getOpenid())) {
            userService.lambdaUpdate()
                    .eq(User::getMobile, mobile)
                    .set(User::getOpenid, openid)
                    .update();
            user.setOpenid(openid);
        }
        return reloadUserProvider.loadUser(ISystem.clientTypeMust(), ISystem.shopIdOpt().getOrNull(), user);
    }

    @Override
    public Class<WechatCode> requestType() {
        return WechatCode.class;
    }

    @Getter
    @Setter
    @ToString
    public static class WechatCode extends AuthenticationRequest {
        /**
         * 登录 授权码
         */
        @NotBlank
        @Crypt
        private String loginCode;
        /**
         * 手机号授权码
         */
        @Crypt
        @NotBlank
        private String mobileCode;
    }
}

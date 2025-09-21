package com.medusa.gruul.service.uaa.service.strategy.login;

import com.alibaba.fastjson2.annotation.JSONField;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.security.server.annotation.GrantType;
import com.medusa.gruul.common.security.server.model.AuthenticationRequest;
import com.medusa.gruul.common.security.server.provider.IAuthenticationProvider;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.constant.SystemHeaders;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.service.uaa.service.mp.entity.User;
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
@GrantType("switch_shop")
@RequiredArgsConstructor
public class SwitchShopAuthenticationProvider implements IAuthenticationProvider<SwitchShopAuthenticationProvider.SwitchShop> {

    private final ReloadUserProvider reloadUserProvider;

    @Override
    public SecureUser<?> authenticate(SwitchShop authentication) throws AuthenticationException {
        //当前客户端类型
        ClientType clientType = ISystem.clientTypeMust();
        //目标客户端类型
        ClientType targetClientType = authentication.getClientType();
        //目标客户端类型是否为空
        boolean targetClientTypeNull = targetClientType == null;
        //如果当前客户端不支持切换店铺 并且 目标客户端类型为空 则抛出异常
        if (!clientType.isSwitchShop() && targetClientTypeNull) {
            throw SecureCodes.REQUEST_INVALID.exception();
        }
        clientType = targetClientTypeNull ? clientType : targetClientType;
        //是否有切换店铺权限
        SecureUser<?> secureUser = ISecurity.userMust();
        SecureCodes.PERMISSION_DENIED.falseThrow(
                ISecurity.matcher()
                        .any(SecureUser::getRoles, Roles.ADMIN, Roles.CUSTOM_ADMIN, Roles.SUPPLIER_ADMIN, Roles.SUPPLIER_CUSTOM_ADMIN, Roles.USER)
                        .match()
        );
        //获取用户信息
        User user = new User()
                .setUsername(secureUser.getUsername())
                .setEmail(secureUser.getEmail())
                .setMobile(secureUser.getMobile())
                .setOpenid(secureUser.getOpenid());
        user.setId(secureUser.getId());
        return reloadUserProvider.loadUser(clientType, authentication.getShopId(), user);
    }

    @Override
    public Class<SwitchShop> requestType() {
        return SwitchShop.class;
    }

    @Getter
    @Setter
    @ToString
    public static class SwitchShop extends AuthenticationRequest {

        /**
         * 目标客户端类型
         */
        @JSONField(name = SystemHeaders.CLIENT_TYPE)
        private ClientType clientType;

        /**
         * 目标店铺id
         */
        private Long shopId;
    }
}

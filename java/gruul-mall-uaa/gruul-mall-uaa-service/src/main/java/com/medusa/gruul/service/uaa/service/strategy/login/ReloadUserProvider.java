package com.medusa.gruul.service.uaa.service.strategy.login;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.model.base.UserKey;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.resource.exception.SecurityException;
import com.medusa.gruul.common.security.server.provider.IReloadUserProvider;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.global.model.constant.SecurityConst;
import com.medusa.gruul.service.uaa.service.model.po.RolesAndPerms;
import com.medusa.gruul.service.uaa.service.mp.entity.Role;
import com.medusa.gruul.service.uaa.service.mp.entity.User;
import com.medusa.gruul.service.uaa.service.mp.service.IUserService;
import com.medusa.gruul.service.uaa.service.service.LastLoginService;
import com.medusa.gruul.service.uaa.service.strategy.perms.PermsStrategyFactory;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * @since 2023/11/16
 */
@Component
@RequiredArgsConstructor
public class ReloadUserProvider implements IReloadUserProvider {

    private final IUserService userService;
    private final ShopRpcService shopRpcService;
    private final LastLoginService lastLoginService;
    private final PermsStrategyFactory permsStrategyFactory;

    /**
     * 根据用户id加载用户权限信息
     *
     * @param preUser 加载之前的用户认证资料
     * @return 用户信息
     */
    @Override
    public SecureUser<?> loadUser(SecureUser<?> preUser) {
        //查询角色
        User user = userService.lambdaQuery().eq(User::getId, preUser.getId()).one();
        if (user == null) {
            throw SecurityException.of(SecureCodes.ACCOUNT_INVALID);
        }
        return loadUser(preUser.getClientType(), preUser.getShopId(), user);
    }

    /**
     * 根据用户加载用户权限信息
     *
     * @param user 用户信息
     * @return 用户权限信息
     */
    public SecureUser<?> loadUser(ClientType clientType, Long shopId, User user) {
        if (user == null) {
            throw SecurityException.of(SecureCodes.USERNAME_PASSWORD_INVALID);
        }
        Long userId = user.getId();
        Long targetShopId = this.checkClient(userId, clientType, shopId);

        /*
         * 查询用户角色与权限
         */

        UserKey userKey = new UserKey(targetShopId, userId);
        RolesAndPerms rolesAndPerms = ISystem.shopId(
                targetShopId,
                () -> permsStrategyFactory.execute(clientType, userKey)
        );
        //主角色
        Set<Role> roles = rolesAndPerms.getRoles();
        SecureUser<?> secureUser = new SecureUser<>()
                .setClientType(clientType)
                .setShopId(targetShopId)
                .setId(userId)
                .setNickname(rolesAndPerms.getNickname())
                .setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .setMobile(user.getMobile())
                .setOpenid(user.getOpenid())
                .setRoles(
                        roles.stream()
                                .map(Role::getValue)
                                .collect(Collectors.toSet())
                )
                .setPerms(rolesAndPerms.getPerms());
        //副角色
        Set<Role> subRoles = rolesAndPerms.getSubRoles();
        if (CollUtil.isNotEmpty(subRoles)) {
            secureUser.setSubRoles(
                    subRoles
                            .stream()
                            .map(Role::getValue)
                            .collect(Collectors.toSet())
            );
        }
        return secureUser;
    }


    /**
     * 检查请求参数
     */
    private Long checkClient(Long userId, ClientType clientType, Long shopId) {
        if (clientType.isSwitchShop() && shopId == null) {
            shopId = this.lastLoginService.getLastLoginShopId(clientType, userId);
        }
        if (shopId == null) {
            throw SecureCodes.USERNAME_PASSWORD_INVALID.exception();
        }
        /*
         * 如果需要检查店铺状态则检查店铺是否可用
         */
        if (clientType.isCheckShop()) {
            ShopInfoVO shopInfo = this.shopRpcService.getShopInfoByShopId(shopId);
            if (shopInfo == null || ShopStatus.NORMAL != shopInfo.getStatus()) {
                lastLoginService.removeLastLoginShopId(clientType, userId);
                return checkClient(userId, clientType, null);
            }
            return shopId;
        }
        //只能是平台 或 消费者端登录  店铺 id 只能是 0
        SecureCodes.REQUEST_INVALID.falseThrow(SecurityConst.NO_SHOP_ID_CLIENT_DEFAULT_SHOP_ID == shopId);
        return shopId;
    }

}

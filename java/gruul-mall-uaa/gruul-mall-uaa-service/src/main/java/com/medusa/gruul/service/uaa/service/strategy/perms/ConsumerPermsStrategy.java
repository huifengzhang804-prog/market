package com.medusa.gruul.service.uaa.service.strategy.perms;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.global.model.constant.SecurityConst;
import com.medusa.gruul.service.uaa.service.mp.entity.Role;
import com.medusa.gruul.service.uaa.service.mp.entity.UserData;
import com.medusa.gruul.service.uaa.service.mp.service.IUserDataService;
import com.medusa.gruul.service.uaa.service.mp.service.IUserRoleService;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.Set;

/**
 * C端登录角色权限处理器
 *
 * @author 张治保
 * @since 2024/4/19
 */
@RequiredArgsConstructor
public class ConsumerPermsStrategy extends AbstractPermsStrategy {
    private final IUserRoleService userRoleService;
    private final IUserDataService userDataService;
    private final Role user = new Role();

    {
        user.setName(Roles.USER.getDesc())
                .setValue(Roles.USER)
                .setClientType(Roles.USER.getClientType())
                .setShopId(SecurityConst.NO_SHOP_ID_CLIENT_DEFAULT_SHOP_ID)
                .setId(Roles.USER.getRoleId());
    }

    @Override
    public String nickname(Roles mainRole, Long shopId, Long userId) {
        Optional<UserData> userData = userDataService.lambdaQuery()
                .select(UserData::getNickname)
                .eq(UserData::getUserId, userId)
                .oneOpt();
        return userData.map(UserData::getNickname).orElse(StrUtil.EMPTY);
    }

    @Override
    public Set<Role> getRoles(Long shopId, Long userId) {
        Set<Role> roles = TenantShop.disable(() -> userRoleService.selectRolesByUserId(userId));
        roles.add(user);
        return roles;
    }

    @Override
    public Set<String> getPerms(Long roleId) {
        return Set.of();
    }

    @Override
    public ClientType getClientType() {
        return ClientType.CONSUMER;
    }

    @Override
    public void afterLoginSuccess(Long shopId, Long userId) {
    }
}

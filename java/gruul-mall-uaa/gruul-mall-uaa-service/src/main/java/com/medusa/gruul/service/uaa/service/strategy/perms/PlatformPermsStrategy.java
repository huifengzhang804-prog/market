package com.medusa.gruul.service.uaa.service.strategy.perms;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.service.uaa.service.mp.entity.Menu;
import com.medusa.gruul.service.uaa.service.mp.entity.Role;
import com.medusa.gruul.service.uaa.service.mp.entity.ShopUserData;
import com.medusa.gruul.service.uaa.service.mp.service.IRoleMenuService;
import com.medusa.gruul.service.uaa.service.mp.service.IShopUserDataService;
import com.medusa.gruul.service.uaa.service.mp.service.IUserRoleService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * @since 2024/4/19
 */
@RequiredArgsConstructor
public class PlatformPermsStrategy extends AbstractPermsStrategy {

    private final IUserRoleService userRoleService;
    private final IRoleMenuService roleMenuService;
    private final IShopUserDataService shopUserDataService;

    @Override
    public String nickname(Roles mainRole, Long shopId, Long userId) {
        if (mainRole.isPermNeed()) {
            return TenantShop.disable(() -> shopUserDataService.lambdaQuery()
                            .select(ShopUserData::getNickname)
                            .eq(ShopUserData::getShopId, shopId)
                            .eq(ShopUserData::getUserId, userId)
                            .oneOpt())
                    .map(ShopUserData::getNickname)
                    .orElse(StrUtil.EMPTY);
        }
        return mainRole.getDesc();
    }

    @Override
    public Set<Role> getRoles(Long shopId, Long userId) {
        Set<Role> rolesSet = userRoleService.selectRolesByUserId(userId);
        if (CollUtil.isEmpty(rolesSet)) {
            return Set.of();
        }
        return rolesSet;
    }

    @Override
    public Set<String> getPerms(Long roleId) {
        List<Menu> menus = roleMenuService.getMenusByRoleId(roleId);
        return CollUtil.emptyIfNull(menus)
                .stream()
                .flatMap(
                        menu -> CollUtil.emptyIfNull(menu.getPerms())
                                .stream()
                ).collect(Collectors.toSet());
    }

    @Override
    public ClientType getClientType() {
        return ClientType.PLATFORM_CONSOLE;
    }

    @Override
    public void afterLoginSuccess(Long shopId, Long userId) {

    }
}

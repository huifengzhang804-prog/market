package com.medusa.gruul.service.uaa.service.strategy.perms;

import com.medusa.gruul.common.model.base.UserKey;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.global.model.strategy.AbstractStrategyFactory;
import com.medusa.gruul.global.model.strategy.IStrategy;
import com.medusa.gruul.service.uaa.service.model.po.RolesAndPerms;
import com.medusa.gruul.service.uaa.service.mp.service.IRoleMenuService;
import com.medusa.gruul.service.uaa.service.mp.service.IShopUserDataService;
import com.medusa.gruul.service.uaa.service.mp.service.IUserDataService;
import com.medusa.gruul.service.uaa.service.mp.service.IUserRoleService;
import com.medusa.gruul.service.uaa.service.service.LastLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author 张治保
 * @since 2024/4/19
 */
@RequiredArgsConstructor
@Component
public class PermsStrategyFactory extends AbstractStrategyFactory<ClientType, UserKey, RolesAndPerms> {

    private final IUserRoleService userRoleService;
    private final IRoleMenuService roleMenuService;
    private final LastLoginService lastLoginService;
    private final IUserDataService userDataService;
    private final IShopUserDataService shopUserDataService;

    @Override
    public Map<ClientType, Supplier<? extends IStrategy<ClientType, UserKey, RolesAndPerms>>> getStrategyMap() {
        return Map.of(
                ClientType.DEVELOPER_CONSOLE, () -> new DeveloperPermsStrategy(userRoleService),
                ClientType.PLATFORM_CONSOLE, () -> new PlatformPermsStrategy(userRoleService, roleMenuService, shopUserDataService),
                ClientType.SHOP_CONSOLE, () -> new ShopPermsStrategy(userRoleService, roleMenuService, lastLoginService, shopUserDataService),
                ClientType.CONSUMER, () -> new ConsumerPermsStrategy(userRoleService, userDataService),
                ClientType.STORE, () -> new ShopStorePermsStrategy(userRoleService),
                ClientType.SUPPLIER_CONSOLE, () -> new SupplierPermsStrategy(userRoleService, roleMenuService, lastLoginService)
        );
    }
}

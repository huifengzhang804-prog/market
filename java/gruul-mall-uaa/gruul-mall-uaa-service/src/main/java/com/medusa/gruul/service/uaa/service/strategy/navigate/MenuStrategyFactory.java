package com.medusa.gruul.service.uaa.service.strategy.navigate;

import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.global.model.strategy.AbstractStrategyFactory;
import com.medusa.gruul.global.model.strategy.IStrategy;
import com.medusa.gruul.service.uaa.service.model.vo.MenuVO;
import com.medusa.gruul.service.uaa.service.mp.service.IMenuService;
import com.medusa.gruul.service.uaa.service.service.RoleMenuHandlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author 张治保
 * @since 2024/4/19
 */
@Component
@RequiredArgsConstructor
public class MenuStrategyFactory extends AbstractStrategyFactory<Roles, Void, List<MenuVO>> {


    private final IMenuService menuService;
    private final RoleMenuHandlerService roleMenuHandlerService;

    @Override
    public Map<Roles, Supplier<? extends IStrategy<Roles, Void, List<MenuVO>>>> getStrategyMap() {
        return Map.of(
                Roles.SUPER_ADMIN, () -> new SuperAdminMenuStrategy(menuService),
                Roles.SUPER_CUSTOM_ADMIN, () -> new SuperCustomAdminMenuStrategy(roleMenuHandlerService),
                Roles.ADMIN, () -> new AdminMenuStrategy(menuService),
                Roles.CUSTOM_ADMIN, () -> new CustomAdminMenuHandler(roleMenuHandlerService),
                Roles.SUPPLIER_CUSTOM_ADMIN, () -> new CustomSupplierMenuStrategy(roleMenuHandlerService),
                Roles.SUPPLIER_ADMIN, () -> new SupplierMenuStrategy(menuService)
        );
    }
}

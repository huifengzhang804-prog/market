package com.medusa.gruul.service.uaa.service.strategy.navigate;

import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.global.model.strategy.IStrategy;
import com.medusa.gruul.service.uaa.service.model.vo.MenuVO;
import com.medusa.gruul.service.uaa.service.mp.service.IMenuService;
import com.medusa.gruul.service.uaa.service.service.RoleMenuHandlerService;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * 自定义供应商管理员
 */
@RequiredArgsConstructor
public class CustomSupplierMenuStrategy implements IStrategy<Roles, Void, List<MenuVO>> {

    private final RoleMenuHandlerService roleMenuHandlerService;

    @Override
    public List<MenuVO> execute(Roles type, Void param) {
        return roleMenuHandlerService.getCustomAdminMenus(Roles.SUPPLIER_CUSTOM_ADMIN, ISecurity.userMust().getId());
    }
}

package com.medusa.gruul.service.uaa.service.strategy.navigate;

import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.global.model.strategy.IStrategy;
import com.medusa.gruul.service.uaa.service.model.vo.MenuVO;
import com.medusa.gruul.service.uaa.service.mp.service.IMenuService;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author 张治保
 * date 2022/3/3
 */
@RequiredArgsConstructor
public class AdminMenuStrategy implements IStrategy<Roles, Void, List<MenuVO>> {

    private final IMenuService menuService;

    @Override
    public List<MenuVO> execute(Roles type, Void param) {
        return menuService.menuTree(ClientType.SHOP_CONSOLE, 0L, (Boolean) null);
    }
}

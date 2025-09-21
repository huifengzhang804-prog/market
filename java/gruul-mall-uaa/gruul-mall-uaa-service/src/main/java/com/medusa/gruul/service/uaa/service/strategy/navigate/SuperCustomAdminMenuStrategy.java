package com.medusa.gruul.service.uaa.service.strategy.navigate;

import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.strategy.IStrategy;
import com.medusa.gruul.service.uaa.service.model.vo.MenuVO;
import com.medusa.gruul.service.uaa.service.service.RoleMenuHandlerService;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 自定义哦超级管理员菜单查询策略
 *
 * @author 张治保
 * date 2022/3/3
 */
@RequiredArgsConstructor
public class SuperCustomAdminMenuStrategy implements IStrategy<Roles, Void, List<MenuVO>> {

    private final RoleMenuHandlerService roleMenuHandlerService;

    @Override
    public List<MenuVO> execute(Roles type, Void param) {
        return roleMenuHandlerService.getCustomAdminMenus(Roles.SUPER_CUSTOM_ADMIN, ISecurity.userMust().getId());
    }
}

package com.medusa.gruul.service.uaa.service.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.service.uaa.service.mp.entity.Menu;
import com.medusa.gruul.service.uaa.service.mp.entity.RoleMenu;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * <p>
 * 角色菜单表 服务类
 * </p>
 *
 * @author 张治保
 * @since 2022-02-23
 */
public interface IRoleMenuService extends IService<RoleMenu> {
    /**
     * 根据roleId获取 menu列表
     * @param roleId 角色id
     * @return 角色拥有的菜单列表
     */
    List<Menu> getMenusByRoleId(@NonNull Long roleId);
}

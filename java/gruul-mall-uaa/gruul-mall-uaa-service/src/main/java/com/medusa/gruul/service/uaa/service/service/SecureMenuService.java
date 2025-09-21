package com.medusa.gruul.service.uaa.service.service;


import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.service.uaa.service.model.dto.MenuDTO;
import com.medusa.gruul.service.uaa.service.model.vo.MenuVO;
import com.medusa.gruul.service.uaa.service.model.vo.NavigateVO;

import java.util.List;

/**
 * 导航菜单服务
 *
 * @author 张治保
 * date 2022/3/2
 */
public interface SecureMenuService {
    /**
     * 查询导航列表 树结构
     *
     * @param role 当前用户角色
     * @return 查询结果
     */
    NavigateVO navigate(Roles role);

    /**
     * 开发者查询菜单
     *
     * @param clientType 客户端类型
     * @return 查询结果
     */
    List<MenuVO> menusForDev(ClientType clientType);

    /**
     * 创建新菜单
     *
     * @param menu 菜单信息
     */
    void newMenu(MenuDTO menu);

    /**
     * 编辑菜单
     *
     * @param menuId 菜单id
     * @param menu   菜单信息
     */
    void editMenu(Long menuId, MenuDTO menu);

    /**
     * 删除菜单
     *
     * @param menuId 菜单id
     */
    void deleteMenu(Long menuId);

}

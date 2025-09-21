package com.medusa.gruul.service.uaa.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.service.uaa.api.dto.MenuQueryDTO;
import com.medusa.gruul.service.uaa.service.model.dto.RoleMenuDTO;
import com.medusa.gruul.service.uaa.service.model.vo.MenuVO;
import com.medusa.gruul.service.uaa.service.mp.entity.Role;
import jakarta.validation.constraints.Min;

import java.util.List;
import java.util.Set;

/**
 * @author 张治保
 * date 2022/4/27
 */
public interface RoleMenuHandlerService {

    /**
     * 分页查询店铺橘色列表
     *
     * @param page 分页参数
     * @return 分页查询结果
     */
    IPage<Role> shopRolePage(MenuQueryDTO page);

    /**
     * 新增角色与权限关联关系
     *
     * @param roleMenu 角色菜单关联数据
     */
    void newRoleMenu(RoleMenuDTO roleMenu);

    /**
     * 编辑角色菜单关联关系
     *
     * @param roleId   角色id
     * @param roleMenu 角色菜单关联数据
     */
    void editRoleMenu(Long roleId, RoleMenuDTO roleMenu);

    /**
     * 删除角色菜单对应关系
     *
     * @param roleId 角色id
     */
    void deleteRoleMenu(Long roleId);

    /**
     * 查询可分配自定义管理员的菜单列表
     *
     * @return 查询结果
     */
    List<MenuVO> menuTree();

    /**
     * 根据角色id获取角色对应的菜单id集合
     *
     * @param roleId 角色id
     * @return 角色对应的菜单id集合
     */
    Set<Long> getMenuIdsByRoleId(Long roleId);

    /**
     * 根据自定义管理员角色与用户id查询 自定义管理员菜单列表
     *
     * @param role   自定义管理员角色
     * @param userId 用户id
     * @return 查询结果
     */
    List<MenuVO> getCustomAdminMenus(Roles role, Long userId);

    /**
     * 更新角色的启用/禁用状态
     * @param roleId
     * @param status
     */
    void updateRoleStatus(@Min(7) Long roleId, Boolean status);
}

package com.medusa.gruul.service.uaa.service.model.dto;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.service.uaa.service.model.enums.UaaError;
import com.medusa.gruul.service.uaa.service.mp.entity.Role;
import com.medusa.gruul.service.uaa.service.mp.entity.RoleMenu;
import com.medusa.gruul.service.uaa.service.mp.service.IRoleMenuService;
import com.medusa.gruul.service.uaa.service.mp.service.IRoleService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2022/4/27
 */
@Getter
@Setter
@ToString
public class RoleMenuDTO {
    /**
     * 角色名称
     */
    @NotBlank
    @Size(min = 1, max = 50)
    private String roleName;
    /**
     * 菜单 id列表
     */
    @NotNull
    @Size(min = 1)
    private Set<Long> menuIds;

    public Long newRole(Roles value, IRoleService roleService) {
        String roleName = getRoleName();
        boolean exists = roleService.lambdaQuery().eq(Role::getName, roleName).exists();
        if (exists) {
            throw UaaError.ROLE_NAME_HAS_BEEN_USED.exception();
        }
        Role role = new Role()
                .setValue(value)
                .setName(roleName)
                .setEnabled(Boolean.TRUE)
                .setClientType(value.getClientType());

        boolean success = roleService.save(role);
        if (!success) {
            throw SystemCode.DATA_ADD_FAILED.exception();
        }
        return role.getId();
    }

    public void newRoleMenus(Long roleId, IRoleMenuService roleMenuService) {
        boolean success = roleMenuService.saveBatch(
                getMenuIds().stream().map(
                        menuId -> new RoleMenu()
                                .setRoleId(roleId)
                                .setMenuId(menuId)
                ).collect(Collectors.toList())
        );
        SystemCode.DATA_ADD_FAILED.falseThrow(success);
    }

    public void updateRole(Long roleId, IRoleService roleService) {
        String roleName = getRoleName();
        boolean exists = roleService.lambdaQuery()
                .eq(Role::getName, roleName)
                .ne(Role::getId, roleId)
                .exists();
        if (exists) {
            throw UaaError.ROLE_NAME_HAS_BEEN_USED.exception();
        }
        Role role = roleService.getById(roleId);
        if (role == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        boolean success = roleService.updateById(role.setName(roleName));
        if (!success) {
            throw SystemCode.DATA_UPDATE_FAILED.exception();
        }
    }

    public void updateRoleMenus(Long roleId, IRoleMenuService roleMenuService) {
        Set<Long> menuIds = getMenuIds();
        Set<Long> existedMenuIds = roleMenuService.lambdaQuery()
                .eq(RoleMenu::getRoleId, roleId)
                .list()
                .stream()
                .map(RoleMenu::getMenuId)
                .collect(Collectors.toSet());

        //-----------取差集------------------
        /* 需要删除的数据
         */
        boolean success;
        Collection<Long> removeMenuIds = CollUtil.subtract(existedMenuIds, menuIds);
        if (CollUtil.isNotEmpty(removeMenuIds)) {
            success = roleMenuService.lambdaUpdate()
                    .eq(RoleMenu::getRoleId, roleId)
                    .in(RoleMenu::getMenuId, removeMenuIds)
                    .remove();
            if (!success) {
                throw SystemCode.DATA_UPDATE_FAILED.exception();
            }
        }
        /* 需要新增的数据
         */
        Collection<Long> newMenuIds = CollUtil.subtract(menuIds, existedMenuIds);
        if (CollUtil.isEmpty(newMenuIds)) {
            return;
        }
        List<RoleMenu> newRoleMenus = newMenuIds.stream()
                .map(
                        menuId -> new RoleMenu().setRoleId(roleId).setMenuId(menuId)
                ).collect(Collectors.toList());
        roleMenuService.saveBatch(newRoleMenus);
    }
}

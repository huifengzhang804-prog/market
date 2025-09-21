package com.medusa.gruul.service.uaa.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.global.model.o.Final;
import com.medusa.gruul.service.uaa.api.dto.MenuQueryDTO;
import com.medusa.gruul.service.uaa.service.model.dto.RoleMenuDTO;
import com.medusa.gruul.service.uaa.service.model.enums.UaaError;
import com.medusa.gruul.service.uaa.service.model.vo.MenuVO;
import com.medusa.gruul.service.uaa.service.mp.entity.Role;
import com.medusa.gruul.service.uaa.service.mp.entity.RoleMenu;
import com.medusa.gruul.service.uaa.service.mp.entity.UserRole;
import com.medusa.gruul.service.uaa.service.mp.service.IMenuService;
import com.medusa.gruul.service.uaa.service.mp.service.IRoleMenuService;
import com.medusa.gruul.service.uaa.service.mp.service.IRoleService;
import com.medusa.gruul.service.uaa.service.mp.service.IUserRoleService;
import com.medusa.gruul.service.uaa.service.service.RoleMenuHandlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2022/4/27
 */
@Service
@RequiredArgsConstructor
public class RoleMenuHandlerServiceImpl implements RoleMenuHandlerService {

	private final IMenuService menuService;
	private final IRoleService roleService;
	private final IRoleMenuService roleMenuService;
	private final IUserRoleService userRoleService;

	@Override
	public IPage<Role> shopRolePage(MenuQueryDTO page) {
		Final<Roles> box = new Final<>();
		ISecurity.match()
				.ifAnySuperAdmin(secureUser -> box.set(Roles.SUPER_CUSTOM_ADMIN))
				.ifAnySupplierAdmin(secureUser -> box.set(Roles.SUPPLIER_CUSTOM_ADMIN))
				.ifAnyShopAdmin(secureUser -> box.set(Roles.CUSTOM_ADMIN));
		return roleService.lambdaQuery()
				.like(Objects.nonNull(page.getName()),Role::getName, page.getName())
				.eq(Objects.nonNull(page.getEnable()),Role::getEnabled,page.getEnable())
				.eq(Role::getValue, box.get())
				.page(page);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void newRoleMenu(RoleMenuDTO roleMenu) {
		Final<Roles> box = new Final<>();
		ISecurity.match()
				.ifAnySuperAdmin(secureUser -> box.set(Roles.SUPER_CUSTOM_ADMIN))
				.ifAnySupplierAdmin(secureUser -> box.set(Roles.SUPPLIER_CUSTOM_ADMIN))
				.ifAnyShopAdmin(secureUser -> box.set(Roles.CUSTOM_ADMIN));
		/*
		 * 保存角色 并获取role id
		 */
		Long roleId = roleMenu.newRole(box.get(), roleService);
		/*
		 * 保存角色与菜单对应关系
		 */
		roleMenu.newRoleMenus(roleId, roleMenuService);

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void editRoleMenu(Long roleId, RoleMenuDTO roleMenu) {
		roleMenu.updateRole(roleId, roleService);
		roleMenu.updateRoleMenus(roleId, roleMenuService);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteRoleMenu(Long roleId) {
		Role role = roleService.getById(roleId);
		if (role == null) {
			throw SystemCode.DATA_DELETE_FAILED.exception();
		}
		if (userRoleService.lambdaQuery()
				.eq(UserRole::getRoleId, roleId)
				.count() > 0) {
			throw UaaError.ROLE_HAS_USER.exception();
		}
		Roles value = role.getValue();
		if (Roles.SUPER_ADMIN == value || Roles.ADMIN == value) {
			throw UaaError.SPECIAL_ROLE_CANNOT_DELETE.exception();
		}
		boolean success = roleService.removeById(roleId);
		if (!success) {
			throw SystemCode.DATA_DELETE_FAILED.exception();
		}
		success = roleMenuService.lambdaUpdate()
				.eq(RoleMenu::getRoleId, roleId)
				.remove();
		if (!success) {
			throw SystemCode.DATA_DELETE_FAILED.exception();
		}
	}

	@Override
	public List<MenuVO> menuTree() {
		Final<ClientType> box = new Final<>();
		ISecurity.match()
				.ifAnySuperAdmin(secureUser -> box.set(ClientType.PLATFORM_CONSOLE))
				.ifAnySupplierAdmin(secureUser -> box.set(ClientType.SUPPLIER_CONSOLE))
				.ifAnyShopAdmin(secureUser -> box.set(ClientType.SHOP_CONSOLE));
		return menuService.menuTree(box.get(), 0L, Boolean.FALSE);
	}

	@Override
	public Set<Long> getMenuIdsByRoleId(Long roleId) {
		return CollUtil.emptyIfNull(
						roleMenuService.lambdaQuery()
								.select(RoleMenu::getMenuId)
								.eq(RoleMenu::getRoleId, roleId)
								.list()
				)
				.stream()
				.map(RoleMenu::getMenuId)
				.collect(Collectors.toSet());
	}

	@Override
	public List<MenuVO> getCustomAdminMenus(Roles role, Long userId) {
		Long customerAdminRoleId = userRoleService.customerAdminRoleId(role, userId);
		if (customerAdminRoleId == null) {
			return Collections.emptyList();
		}
		List<RoleMenu> roleMenus = roleMenuService.lambdaQuery()
				.eq(RoleMenu::getRoleId, customerAdminRoleId)
				.list();
		if (CollUtil.isEmpty(roleMenus)) {
			return Collections.emptyList();
		}
		Set<Long> menuIds = roleMenus.stream().map(RoleMenu::getMenuId).collect(Collectors.toSet());
		return menuService.menuTree(role.getClientType(), 0L, menuIds);
	}

	@Override
	public void updateRoleStatus(Long roleId, Boolean status) {
		roleService.lambdaUpdate()
				.set(Role::getEnabled, status)
				.eq(Role::getId, roleId)
				.update();
	}


}

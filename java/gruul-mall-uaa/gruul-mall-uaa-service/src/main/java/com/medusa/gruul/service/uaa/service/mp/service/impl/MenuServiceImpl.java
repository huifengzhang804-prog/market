package com.medusa.gruul.service.uaa.service.mp.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.security.model.enums.MenuType;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.service.uaa.service.model.vo.MenuVO;
import com.medusa.gruul.service.uaa.service.mp.entity.Menu;
import com.medusa.gruul.service.uaa.service.mp.mapper.MenuMapper;
import com.medusa.gruul.service.uaa.service.mp.service.IMenuService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author 张治保
 * @since 2022-02-23
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

	@Override
	public List<MenuVO> queryMenusByParentId(ClientType clientType, Long parentId, Boolean unshared) {
		return baseMapper.queryMenusByParentId(clientType, parentId, unshared);
	}

	@Override
	public List<MenuVO> queryCustomAdminMenus(ClientType clientType, Long parentId, Set<Long> menuIds) {
		return baseMapper.queryCustomAdminMenus(clientType, parentId, menuIds);
	}

	@Override
	public List<MenuVO> menuTree(ClientType clientType, Long parentId, Boolean unshared) {
		List<MenuVO> menus = this.queryMenusByParentId(clientType, parentId, unshared);
		if (CollUtil.isEmpty(menus)) {
			return Collections.emptyList();
		}
		menus.stream()
				.filter(menu -> MenuType.CATALOG == menu.getType())
				.forEach(
						menu -> menu.setChildren(menuTree(clientType, menu.getId(), unshared))
				);
		return menus;
	}

	@Override
	public List<MenuVO> menuTree(ClientType clientType, Long parentId, Set<Long> menuIds) {
		if (CollUtil.isEmpty(menuIds)) {
			return Collections.emptyList();
		}
		List<MenuVO> menus = this.queryCustomAdminMenus(clientType, parentId, menuIds);
		if (CollUtil.isEmpty(menus)) {
			return Collections.emptyList();
		}
		menus.forEach(
				menu -> menuIds.remove(menu.getId())
		);

		return menus.stream()
				.filter(
						menu -> {
							if (MenuType.CATALOG == menu.getType()) {
								menu.setChildren(menuTree(clientType, menu.getId(), menuIds));
								return CollUtil.isNotEmpty(menu.getChildren());
							}
							return true;
						}
				).collect(Collectors.toList());
	}
}

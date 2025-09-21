package com.medusa.gruul.service.uaa.service.service.impl;

import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.service.uaa.service.mp.entity.Role;
import com.medusa.gruul.service.uaa.service.mp.entity.User;
import com.medusa.gruul.service.uaa.service.mp.entity.UserRole;
import com.medusa.gruul.service.uaa.service.mp.service.IRoleService;
import com.medusa.gruul.service.uaa.service.mp.service.IUserRoleService;
import com.medusa.gruul.service.uaa.service.mp.service.IUserService;
import com.medusa.gruul.service.uaa.service.service.LiveAnchorService;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author miskw
 * @date 2023/6/13
 * @describe 描述
 */
@Service
@RequiredArgsConstructor
public class LiveAnchorServiceImpl implements LiveAnchorService {
	private final IUserService userService;
	private final IRoleService roleService;
	private final IUserRoleService userRoleService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addAnchorAuthority(Long userId) {
		User user = Option.of(
						userService.lambdaQuery()
								.eq(User::getId, userId)
								.one())
				.get();
		Role role = new Role()
				.setName(Roles.ANCHOR.getDesc())
				.setValue(Roles.ANCHOR)
				.setClientType(ClientType.CONSUMER)
				.setShopId(ISecurity.userMust().getShopId());
		roleService.save(role);
		userRoleService.save(
				new UserRole()
						.setUserId(user.getId())
						.setRoleId(role.getId())
						.setEnable(Boolean.TRUE));
	}
}

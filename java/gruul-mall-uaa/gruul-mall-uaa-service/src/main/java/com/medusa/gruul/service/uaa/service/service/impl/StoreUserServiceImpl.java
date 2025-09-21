package com.medusa.gruul.service.uaa.service.service.impl;

import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.service.uaa.service.mp.entity.Role;
import com.medusa.gruul.service.uaa.service.mp.entity.User;
import com.medusa.gruul.service.uaa.service.mp.entity.UserRole;
import com.medusa.gruul.service.uaa.service.mp.service.IRoleService;
import com.medusa.gruul.service.uaa.service.mp.service.IUserRoleService;
import com.medusa.gruul.service.uaa.service.service.StoreUserService;
import com.medusa.gruul.service.uaa.service.service.UserDataHandlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 门店用户实现层
 *
 * @author xiaoq
 * @Description 门店用户实现层
 * @date 2023-03-09 18:40
 */
@Service
@RequiredArgsConstructor
public class StoreUserServiceImpl implements StoreUserService {

	private final IRoleService roleService;

	private final IUserRoleService userRoleService;

	private final UserDataHandlerService userDataHandlerService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void checkStoreUserByMobile(List<String> mobiles) {
		for (String mobile : mobiles) {
			//获取用户创建信息
			User user = userDataHandlerService.generateUser(mobile, null);
			Role role = new Role()
					.setEnabled(Boolean.TRUE)
					.setName(Roles.SHOP_STORE.getDesc())
					.setValue(Roles.SHOP_STORE)
					.setClientType(ClientType.STORE)
					.setShopId(ISecurity.userMust().getShopId());
			roleService.save(role);
			userRoleService.save(
					new UserRole()
							.setUserId(user.getId())
							.setRoleId(role.getId())
							.setEnable(Boolean.TRUE)
			);
		}
	}
}

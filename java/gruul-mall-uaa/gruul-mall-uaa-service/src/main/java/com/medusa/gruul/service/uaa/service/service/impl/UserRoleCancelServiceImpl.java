package com.medusa.gruul.service.uaa.service.service.impl;

import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.service.uaa.service.model.po.UserRoleDetailsInfo;
import com.medusa.gruul.service.uaa.service.mp.entity.Role;
import com.medusa.gruul.service.uaa.service.mp.entity.User;
import com.medusa.gruul.service.uaa.service.mp.service.IRoleService;
import com.medusa.gruul.service.uaa.service.mp.service.IUserRoleService;
import com.medusa.gruul.service.uaa.service.mp.service.IUserService;
import com.medusa.gruul.service.uaa.service.service.UserRoleCancelService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户角色取消服务实现层
 *
 * @author xiaoq
 * Description 用户角色取消服务实现层
 * date 2023-03-14 19:54
 */
@Service
@RequiredArgsConstructor
public class UserRoleCancelServiceImpl implements UserRoleCancelService {

	private final IUserService userService;

	private final IRoleService roleService;

	private final IUserRoleService userRoleService;

	/**
	 * 删除用户角色 BY 手机号
	 *
	 * @param mobile 用户手机号
	 * @param roles  用户角色
	 * @return 是否成功
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean delUserRoleByMobiles(String mobile, Roles roles) {
		if (roles == Roles.USER) {
			return true;
		}
		User user = userService.lambdaQuery().eq(User::getMobile, mobile).one();
		if (user == null) {
			throw SystemCode.DATA_NOT_EXIST.exception();
		}
		UserRoleDetailsInfo userRoleDetails = userRoleService.getUserRoleDetails(user.getId(), roles.getValue());
		if (Objects.isNull(userRoleDetails)) {
            return Boolean.TRUE;
        }
		Role role = roleService.getById(userRoleDetails.getRoleId());
		if (role != null) {
			roleService.removeById(userRoleDetails.getRoleId());
			userRoleService.removeById(userRoleDetails.getUserRoleId());
			//用户下线
			ISecurity.offlineUsers(ClientType.STORE, role.getShopId(), user.getId());
		}
		return Boolean.TRUE;
	}
}

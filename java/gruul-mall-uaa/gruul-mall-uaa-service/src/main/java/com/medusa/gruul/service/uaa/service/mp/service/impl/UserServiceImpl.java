package com.medusa.gruul.service.uaa.service.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.service.uaa.service.model.dto.AvailableUserPageDTO;
import com.medusa.gruul.service.uaa.service.model.vo.UserAnchorVO;
import com.medusa.gruul.service.uaa.service.model.vo.UserWithDataVO;
import com.medusa.gruul.service.uaa.service.mp.entity.User;
import com.medusa.gruul.service.uaa.service.mp.mapper.UserMapper;
import com.medusa.gruul.service.uaa.service.mp.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 张治保
 * @since 2022-02-23
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {


	@Override
	public IPage<UserWithDataVO> getAvailableUserForShopAdmin(AvailableUserPageDTO availableUserPage) {
		return baseMapper.getAvailableUserForShopAdmin(availableUserPage);
	}

	/**
	 * 批量根据用户id查询用户基础资料
	 *
	 * @param userIds         用户id集合
	 * @param currentShopUser 是否是当前店铺的用户
	 * @return 查询结果
	 */
	@Override
	public List<UserWithDataVO> getUserDataBatchByUserId(Set<Long> userIds, boolean currentShopUser) {
		return baseMapper.getUserDataBatchByUserId(userIds, currentShopUser);
	}

	@Override
	public IPage<UserAnchorVO> anchorList(AvailableUserPageDTO availableUserPage) {
		return baseMapper.anchorList(availableUserPage, Roles.ANCHOR);
	}
}

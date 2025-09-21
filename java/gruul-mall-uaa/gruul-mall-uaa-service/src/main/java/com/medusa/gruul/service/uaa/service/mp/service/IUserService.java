package com.medusa.gruul.service.uaa.service.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.service.uaa.service.model.dto.AvailableUserPageDTO;
import com.medusa.gruul.service.uaa.service.model.vo.UserAnchorVO;
import com.medusa.gruul.service.uaa.service.model.vo.UserWithDataVO;
import com.medusa.gruul.service.uaa.service.mp.entity.User;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 张治保
 * @since 2022-02-23
 */
public interface IUserService extends IService<User> {

	/**
	 * 分页查询可用的用户资料列表
	 *
	 * @param availableUserPage 查询数据
	 * @return 查询结果
	 */
	IPage<UserWithDataVO> getAvailableUserForShopAdmin(AvailableUserPageDTO availableUserPage);

	/**
	 * 批量根据用户id查询用户基础资料
	 *
	 * @param userIds         用户id集合
	 * @param currentShopUser 是否是当前店铺的用户
	 * @return 查询结果
	 */
	List<UserWithDataVO> getUserDataBatchByUserId(Set<Long> userIds, boolean currentShopUser);

	/**
	 * 主播列表
	 *
	 * @param availableUserPage 分页查询参数
	 * @return 查询结果
	 */
	IPage<UserAnchorVO> anchorList(AvailableUserPageDTO availableUserPage);
}

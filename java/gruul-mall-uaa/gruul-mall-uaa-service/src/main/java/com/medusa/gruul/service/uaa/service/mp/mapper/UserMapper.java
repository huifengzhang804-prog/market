package com.medusa.gruul.service.uaa.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.service.uaa.service.model.dto.AvailableUserPageDTO;
import com.medusa.gruul.service.uaa.service.model.vo.UserAnchorVO;
import com.medusa.gruul.service.uaa.service.model.vo.UserWithDataVO;
import com.medusa.gruul.service.uaa.service.mp.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author 张治保
 * @since 2022-02-23
 */
public interface UserMapper extends BaseMapper<User> {


	/**
	 * 分页获取店铺管理员可用用户列表
	 *
	 * @param availableUserPage 分页查询参数
	 * @return 查询结果
	 */
	IPage<UserWithDataVO> getAvailableUserForShopAdmin(@Param("page") AvailableUserPageDTO availableUserPage);

	/**
	 * 批量根据用户id查询用户基础资料
	 *
	 * @param userIds         用户id集合
	 * @param currentShopUser 是否是当前店铺的用户
	 * @return 查询结果
	 */
	List<UserWithDataVO> getUserDataBatchByUserId(@Param("userIds") Set<Long> userIds, @Param("currentShopUser") boolean currentShopUser);


	/**
	 * 主播列表
	 *
	 * @param availableUserPage 分页查询参数
	 * @param roles             角色
	 * @return 查询结果
	 */
	IPage<UserAnchorVO> anchorList(@Param("availableUserPage") AvailableUserPageDTO availableUserPage, @Param("roles") Roles roles);
}

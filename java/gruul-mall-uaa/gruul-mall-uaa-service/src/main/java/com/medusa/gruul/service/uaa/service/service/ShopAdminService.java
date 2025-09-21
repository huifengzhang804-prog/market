package com.medusa.gruul.service.uaa.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.service.uaa.service.model.dto.AvailableUserPageDTO;
import com.medusa.gruul.service.uaa.service.model.dto.ShopCustomAdminDTO;
import com.medusa.gruul.service.uaa.service.model.dto.ShopCustomAdminPageDTO;
import com.medusa.gruul.service.uaa.service.model.vo.ShopUserDataVO;
import com.medusa.gruul.service.uaa.service.model.vo.UserAnchorVO;
import com.medusa.gruul.service.uaa.service.model.vo.UserWithDataVO;
import com.medusa.gruul.service.uaa.service.mp.entity.ShopUserData;
import com.medusa.gruul.shop.api.model.dto.ShopAdminMapDTO;
import com.medusa.gruul.shop.api.model.dto.ShopsEnableDisableDTO;

/**
 * @author 张治保
 * date 2022/4/19
 */
public interface ShopAdminService {
	/**
	 * 新增店铺管理员
	 *
	 * @param shopAdminMap 店铺管理员
	 */
	void shopAdminChange(ShopAdminMapDTO shopAdminMap);

	/**
	 * 启用/禁用店铺
	 *
	 * @param shopsEnableDisable 店铺禁用启用参数
	 */
	void shopEnableDisable(ShopsEnableDisableDTO shopsEnableDisable);

	/**
	 * 店铺用户(管理员)资料分页查询
	 *
	 * @param shopCustomAdminPage 分页参数
	 * @return 查询结果
	 */
	IPage<ShopUserDataVO> shopUserDataPage(ShopCustomAdminPageDTO shopCustomAdminPage);

	/**
	 * 新增店铺管理员
	 *
	 * @param customAdmin 管理员数据
	 */
	void newShopCustomAdmin(ShopCustomAdminDTO customAdmin);

	/**
	 * 编辑店铺管理员
	 *
	 * @param dataId      资料id
	 * @param customAdmin 管理员数据
	 */
	void editShopCustomAdmin(Long dataId, ShopCustomAdminDTO customAdmin);

	/**
	 * 启用/禁用 店铺管理员
	 *
	 * @param dataId 资料id
	 * @param enable 启用/禁用
	 */
	void enableDisableShopCustomAdmin(Long dataId, Boolean enable);

	/**
	 * 移除用户和店铺管理员绑定关系
	 *
	 * @param dataId 管理员资料id
	 */
	void deleteShopCustomAdmin(Long dataId);

	/**
	 * 查询当前店铺可用作管理员的用户
	 *
	 * @param availableUserPage 查询条件
	 * @return 查询结果
	 */
	IPage<UserWithDataVO> getAvailableUserForShopAdmin(AvailableUserPageDTO availableUserPage);

	/**
	 * 根据用户id查询管理员注册资料
	 *
	 * @param userId 用户id
	 * @return 查询出的用户资料
	 */
	UserWithDataVO getAdminRegisterDataById(Long userId);

	/**
	 * 获取我的店铺资料
	 *
	 * @return 查询结果
	 */
	ShopUserData myData();
	/**
	 * 分页查询可成为主播的用户
	 *
	 * @param availableUserPage 查询参数
	 * @return 主播列表
	 */
	IPage<UserAnchorVO> anchorList(AvailableUserPageDTO availableUserPage);
}

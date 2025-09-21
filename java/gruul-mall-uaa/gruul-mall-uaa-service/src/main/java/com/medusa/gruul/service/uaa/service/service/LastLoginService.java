package com.medusa.gruul.service.uaa.service.service;

import com.medusa.gruul.common.system.model.model.ClientType;

/**
 * @author 张治保
 * date 2023/7/12
 */
public interface LastLoginService {

	/**
	 * 获取最后一次登录店铺 id
	 *
	 * @param clientType 客户端类型
	 * @param userId     用户 id
	 * @return 最后一次登录店铺 id
	 */
	Long getLastLoginShopId(ClientType clientType, Long userId);


	/**
	 * 保存最后一次登录店铺 id 保存10天
	 *
	 * @param clientType 客户端类型
	 * @param userId     用户 id
	 * @param shopId     店铺 id
	 */
	void saveLastLoginShopId(ClientType clientType, Long userId, Long shopId);

	/**
	 * 移除最后一次登录店铺 id
	 *
	 * @param clientType 客户端类型
	 * @param userId     用户 id
	 */
	void removeLastLoginShopId(ClientType clientType, Long userId);
}

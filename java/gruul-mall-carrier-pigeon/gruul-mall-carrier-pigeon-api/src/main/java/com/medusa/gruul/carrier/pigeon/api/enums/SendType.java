package com.medusa.gruul.carrier.pigeon.api.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.medusa.gruul.common.security.model.constant.SecureConst;
import com.medusa.gruul.common.system.model.model.ClientType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 消息发送方式
 *
 * @author 张治保
 * date 2022/4/29
 */
@Getter
@RequiredArgsConstructor
public enum SendType {

	/**
	 * 广播给平台
	 */
	BROADCAST_PLATFORM(0, ClientType.PLATFORM_CONSOLE, SecureConst.DESTINATION_ALL_USER, Boolean.FALSE),
	/**
	 * 广播给店铺
	 */
	BROADCAST_SHOP(1, ClientType.SHOP_CONSOLE, SecureConst.DESTINATION_ALL_USER, Boolean.FALSE),
	/**
	 * 指定平台
	 */
	MARKED_PLATFORM(2, ClientType.PLATFORM_CONSOLE, SecureConst.DESTINATION_SHOP_USER, Boolean.TRUE),

	/**
	 * 指定平台管理员
	 */
	MARKED_PLATFORM_ADMIN(3, ClientType.PLATFORM_CONSOLE, SecureConst.DESTINATION_SHOP_MARKED_USER, Boolean.TRUE),
	/**
	 * 指定店铺
	 */
	MARKED_SHOP(4, ClientType.SHOP_CONSOLE, SecureConst.DESTINATION_SHOP_USER, Boolean.TRUE),
	/**
	 * 指定店铺管理员
	 */
	MARKED_SHOP_ADMIN(5, ClientType.SHOP_CONSOLE, SecureConst.DESTINATION_SHOP_MARKED_USER, Boolean.TRUE),
	/**
	 * 指定用户
	 */
	MARKED_USER(6, ClientType.CONSUMER, SecureConst.DESTINATION_SHOP_MARKED_USER, Boolean.TRUE),

	/**
	 * 供应商-店铺管理员
	 */
	SUPPLIER_SHOP_ADMIN(7, ClientType.SUPPLIER_CONSOLE, SecureConst.DESTINATION_SHOP_USER, Boolean.TRUE),

	/**
	 * H5用户
	 */
	H5_USER(8, ClientType.CONSUMER, SecureConst.DESTINATION_SHOP_MARKED_USER, Boolean.TRUE);

	@EnumValue
	private final Integer value;

	/**
	 * 客户端类型
	 */
	private final ClientType clientType;

	/**
	 * 发送消息的topic模板
	 */
	private final String destinationTemplate;

	/**
	 * 是否时指定对象的发送方式
	 */
	private final Boolean marked;

	/**
	 * 获取发送目标完整 topic destination
	 *
	 * @return 完整 topic
	 */
	public String getDestination() {
		return getDestination(null, null);
	}

	/**
	 * 获取发送目标完整 topic destination
	 *
	 * @param shopId 店铺 id
	 * @return 完整 topic
	 */
	public String getDestination(Long shopId) {
		return getDestination(shopId, null);
	}

	/**
	 * 获取发送目标完整 topic destination
	 *
	 * @param shopId 店铺 id
	 * @param userId 用户 id
	 * @return 完整 topic
	 */
	public String getDestination(Long shopId, Long userId) {
		return StrUtil.format(getDestinationTemplate(), getClientType(), shopId, userId);
	}
}

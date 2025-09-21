package com.medusa.gruul.common.system.model.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.medusa.gruul.global.model.constant.SecurityConst;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.BiFunction;

/**
 * 客户端类型
 *
 * @author 张治保
 * date 2022/4/12
 */
@Getter
@RequiredArgsConstructor
public enum ClientType {

	/**
	 * 开发者端
	 */
	DEVELOPER_CONSOLE(-1, false, false, (headerShopId, userShopId) -> headerShopId.equals(userShopId) && headerShopId.equals(SecurityConst.NO_SHOP_ID_CLIENT_DEFAULT_SHOP_ID)),

	/**
	 * 平台端
	 */
	PLATFORM_CONSOLE(0, false, false, DEVELOPER_CONSOLE.shopIdCheck),

	/**
	 * 商家端
	 */
	SHOP_CONSOLE(1, true, true, Long::equals),

	/**
	 * 消费端
	 */
	CONSUMER(2, false, false, (headerShopId, userShopId) -> Boolean.TRUE),

	/**
	 * 门店端
	 */
	STORE(3, true, false, CONSUMER.shopIdCheck),

	/**
	 * 供应商端
	 */
	SUPPLIER_CONSOLE(4, true, true, SHOP_CONSOLE.shopIdCheck),
	;

	@EnumValue
	private final Integer value;

	/**
	 * 是否在登录时校验店铺是否可用
	 */
	private final boolean checkShop;

	/**
	 * 当前客户端是否可切换店铺登录
	 */
	private final boolean switchShop;

	/**
	 * 商铺id校验 (headerShopId,userShopId)-> boolean
	 * headerShopId 请求头的 shopId，
	 * userShopId  用户所属的 shopId
	 * return 是否校验通过
	 */
	private final BiFunction<Long, Long, Boolean> shopIdCheck;
}

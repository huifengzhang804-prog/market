package com.medusa.gruul.global.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 分账接收方类型
 *
 * @author 张治保
 */
@Getter
@RequiredArgsConstructor
public enum ReceiverType {

	/**
	 * 商家 商家属于分账方不属于分账对象 不需要分账 剩余资金解冻 自动进入商家账户
	 */
	SHOP(1, "", ""),

	/**
	 * 平台
	 */
	PLATFORM(2, "MERCHANT_ID", "SERVICE_PROVIDER"),

	/**
	 * 个人openid（由父商户APPID转换得到）
	 */
	DISTRIBUTOR(3, "PERSONAL_OPENID", "DISTRIBUTOR");

	@EnumValue
	private final Integer value;

	/**
	 * 1、MERCHANT_ID：商户号
	 * 2、PERSONAL_OPENID：个人openid（由父商户APPID转换得到）
	 */
	private final String type;
	/**
	 * SERVICE_PROVIDER：服务商
	 * STORE：门店
	 * STAFF：员工
	 * STORE_OWNER：店主
	 * PARTNER：合作伙伴
	 * HEADQUARTER：总部
	 * BRAND：品牌方
	 * DISTRIBUTOR：分销商
	 * USER：用户
	 * SUPPLIER： 供应商
	 * CUSTOM：自定义
	 */
	private final String relations;

}
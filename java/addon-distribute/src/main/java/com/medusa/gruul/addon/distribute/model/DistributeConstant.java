package com.medusa.gruul.addon.distribute.model;

/**
 * @author 张治保
 * date 2022/11/14
 */
public interface DistributeConstant {

	String NO = "distribute";

	/**
	 * 商家端商品绑定信息调整 分布式锁
	 */
	String DISTRIBUTE_PRODUCT_BIND_LOCK = "addon:distribute:product:bind:lock";

	/**
	 * 分销配置缓存key
	 */
	String DISTRIBUTE_CONFIG_CACHE_KEY = "addon:distribute:config";

	/**
	 * 分销配置调整 分布式锁
	 */
	String DISTRIBUTE_CONFIG_UPDATE_LOCK_KEY = "addon:distribute:config:update:lock";

	/**
	 * 更新店铺信息分布式锁
	 */
	String UPDATE_SHOP_LOCK_KEY = "addon:distribute:shop:update";

	/**
	 * 扫码成为分销员分布式锁
	 */
	String DISTRIBUTOR_LOCK_KEY = "addon:distribute:distributor";

	/**
	 * 修改分销员佣金分布式锁
	 */
	String DISTRIBUTOR_BONUS_LOCK_KEY = "addon:distribute:distributor:bonus";



	/**
	 * 发送短信验证码
	 */
	String DISTRIBUTE_SEND_SMS_CODE = "addon:distribute:send:sms";

	/**
	 * 分销订单状态调整
	 */
	String ORDER_STATUS_UPDATE_LOCK_KEY = "distribute:order:status:update";

	/**
	 * 已提现佣金更新
	 */
	String TOTAL_INCREMENT_SQL_TEMPLATE = "total = total+{}";

	/**
	 * 未提现佣金更新
	 */
	String UNDRAWN_INCREMENT_SQL_TEMPLATE = "undrawn = undrawn+{}";

	/**
	 * 未结算佣金更新
	 */
	String UNSETTLED_INCREMENT_SQL_TEMPLATE = "unsettled = unsettled+{}";

	/**
	 * 无效佣金更新
	 */
	String INVALID_INCREMENT_SQL_TEMPLATE = "invalid = invalid+{}";

	/**
	 * 修改商品销量分布式锁
	 */
	String PRODUCT_SALES_LOCK_KEY = "addon:distribute:product:sales";

	/**
	 * 更新商品销量
	 */
	String PRODUCT_SALES_INCREMENT_SQL_TEMPLATE = "sales = sales+{}";
}

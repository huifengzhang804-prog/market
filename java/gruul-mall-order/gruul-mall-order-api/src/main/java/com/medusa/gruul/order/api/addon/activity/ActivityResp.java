package com.medusa.gruul.order.api.addon.activity;

import cn.hutool.json.JSONObject;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.model.base.StackableDiscount;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * 活动请求参数
 *
 * @author 张治保
 * <p>
 * date 2023/3/15
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ActivityResp implements Serializable {

	/**
	 * 额外信息
	 */
	private JSONObject extra = new JSONObject();

	/**
	 * sku对应的价格 map
	 */
	private Map<ShopProductSkuKey, Long> skuKeyPriceMap = Collections.emptyMap();

	/**
	 * 是否可用优惠
	 */
	private StackableDiscount stackable = new StackableDiscount();


	/**
	 * 普通订单初始化
	 */
	public ActivityResp commonOrder() {
		getStackable().setPayTimeout(null)
				.setCoupon(true)
				.setFull(true)
				.setVip(true);
		return this;
	}

}

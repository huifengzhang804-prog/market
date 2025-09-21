package com.medusa.gruul.addon.distribute.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * date 2023/5/15
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode
public class ShopOrderKey implements java.io.Serializable {

	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 店铺id
	 */
	private Long shopId;

}

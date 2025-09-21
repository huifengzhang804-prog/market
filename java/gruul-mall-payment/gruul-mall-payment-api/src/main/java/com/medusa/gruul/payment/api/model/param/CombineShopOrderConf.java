package com.medusa.gruul.payment.api.model.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/6/7
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class CombineShopOrderConf implements Serializable {
	/**
	 * 店铺id
	 */
	private Long shopId;

	/**
	 * 是否可分账
	 */
	private Boolean sharing;
}

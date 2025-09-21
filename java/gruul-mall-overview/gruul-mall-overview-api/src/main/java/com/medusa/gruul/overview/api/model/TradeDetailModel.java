package com.medusa.gruul.overview.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2022/11/24
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class TradeDetailModel implements Serializable {


	/**
	 * 售后工单号
	 */
	private String afsNo;


	/**
	 * 包裹id
	 */
	private Long packageId;

	/**
	 * 订单商品项id
	 */
	private Long shopOrderItemId;


}

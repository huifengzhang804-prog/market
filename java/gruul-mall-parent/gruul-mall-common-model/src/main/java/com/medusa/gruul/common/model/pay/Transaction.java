package com.medusa.gruul.common.model.pay;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 支付流水信息
 *
 * @author 张治保
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Transaction implements Serializable {
	/**
	 * 交易流水号
	 */
	private String transactionId;

	/**
	 * 是否可分账
	 */
	private Boolean profitSharing;
}
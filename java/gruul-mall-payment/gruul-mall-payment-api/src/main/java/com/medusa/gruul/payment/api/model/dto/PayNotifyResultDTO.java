package com.medusa.gruul.payment.api.model.dto;

import com.medusa.gruul.common.model.pay.Transaction;
import com.medusa.gruul.payment.api.enums.PayOrderType;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * PayNotifyResultDTO.java
 *
 * @author xiaoq
 * Description
 * date 2022-08-01 18:48
 */
@Data
@ToString
@Accessors(chain = true)
public class PayNotifyResultDTO implements Serializable {

	@Serial
	private static final long serialVersionUID = -4174931221212256753L;

	/**
	 * 支付订单类型
	 */
	private PayOrderType payOrderType;

	/**
	 * 业务订单号
	 */
	private String outTradeNo;

	/**
	 * 附加数据,格式为json字符串,怎么发送怎么返回
	 */
	private PaymentInfoDTO businessParams;

	/**
	 * 店铺id 对应支付订单号(支付流水号)
	 */
	private Map<Long, Transaction> shopIdTransactionMap;


	public Transaction shopTransaction(Long shopId) {
		Transaction transaction = shopIdTransactionMap.get(shopId);
		return transaction == null ? shopIdTransactionMap.get(0L) : transaction;
	}


}

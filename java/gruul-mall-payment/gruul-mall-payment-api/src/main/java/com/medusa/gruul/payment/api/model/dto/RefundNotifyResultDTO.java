package com.medusa.gruul.payment.api.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * RefundNotifyResultDTO.java
 *
 * @author xiaoq
 * @Description RefundNotifyResultDTO.java
 * @date 2022-08-08 15:46
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class RefundNotifyResultDTO implements Serializable {
	private static final long serialVersionUID = 5586352634884744140L;

	/**
	 * 业务订单号
	 */
	private String outTradeNo;

	/**
	 * 售后单号
	 */
	private String afsNum;

	/**
	 * 退款单号.
	 */
	private String refundNum;
}

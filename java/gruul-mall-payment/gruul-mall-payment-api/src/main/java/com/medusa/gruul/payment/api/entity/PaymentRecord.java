package com.medusa.gruul.payment.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 支付记录表
 *
 * @author xiaoq
 * @ Description PaymentRecord.java
 * @date 2022-08-01 16:29
 */

@Data
@Accessors(chain = true)
@TableName(value = "t_payment_record", excludeProperty = "deleted")
@EqualsAndHashCode(callSuper = true)
public class PaymentRecord extends BaseEntity {

	/**
	 * 请求对象数据
	 */
	@TableField("request_params")
	private String requestParams;

	/**
	 * 最终发送数据
	 */
	@TableField("send_param")
	private String sendParam;

	/**
	 * 第三方回调的数据
	 */
	@TableField("notify_param")
	private String notifyParam;

	/**
	 * 支付 记录表id
	 */
	@TableField("payment_id")
	private Long paymentId;
}

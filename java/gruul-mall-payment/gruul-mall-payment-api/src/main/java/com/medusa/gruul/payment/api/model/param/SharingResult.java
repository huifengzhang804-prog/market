package com.medusa.gruul.payment.api.model.param;

import cn.hutool.json.JSONObject;
import com.medusa.gruul.payment.api.enums.SharingStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 分账结果
 *
 * @author 张治保
 * date 2023/6/21
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SharingResult implements Serializable {

	/**
	 * 分账单号
	 */
	private String sharingNo;

	/**
	 * 分账状态
	 */
	private SharingStatus status;

	/**
	 * error detail
	 */
	private JSONObject error;

	/**
	 * 分账接收方分账结果详情
	 */
	private Map<ReceiverKey, ReceiverResult> receiverResultMap;


	@Getter
	@Setter
	@ToString
	@Accessors(chain = true)
	public static class ReceiverResult implements Serializable {

		/**
		 * 枚举值：
		 * 1、PENDING：待分账
		 * 2、SUCCESS：分账成功
		 * 3、CLOSED：已关闭
		 */
		private String result;

		/**
		 * 分账明细单号
		 * 微信分账明细单号，每笔分账业务执行的明细单号，可与资金账单对账使用
		 */
		private String detailId;

		/**
		 * 分账失败原因，当分账结果result为CLOSED（已关闭）时，返回该字段
		 * 枚举值：
		 * 1、ACCOUNT_ABNORMAL：分账接收账户异常
		 * 2、NO_RELATION：分账关系已解除
		 * 3、RECEIVER_HIGH_RISK：高风险接收方
		 * 4、RECEIVER_REAL_NAME_NOT_VERIFIED：接收方未实名
		 * 5、NO_AUTH：分账权限已解除
		 * 6、RECEIVER_RECEIPT_LIMIT：超出用户月收款限额
		 * 7、PAYER_ACCOUNT_ABNORMAL：分出方账户异常
		 * 8、INVALID_REQUEST: 描述参数设置失败
		 */
		private String failReason;

		/**
		 * 分账创建时间
		 */
		private LocalDateTime createTime;

		/**
		 * 分账完成时间
		 */
		private LocalDateTime finishTime;
	}
}

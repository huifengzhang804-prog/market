package com.medusa.gruul.payment.api.model.param;

import com.medusa.gruul.global.model.enums.ReceiverType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/6/21
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
public class ReceiverKey implements Serializable {

	/**
	 * 分账接收方类型
	 */
	private ReceiverType receiverType;

	/**
	 * 分账接收方账号
	 */
	private Long accountId;
}

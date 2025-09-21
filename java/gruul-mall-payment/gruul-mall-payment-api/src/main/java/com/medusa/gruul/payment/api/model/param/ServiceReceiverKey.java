package com.medusa.gruul.payment.api.model.param;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 服务商 接收方类型 key
 *
 * @author 张治保
 * date 2023/6/21
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
public class ServiceReceiverKey implements Serializable {
	/**
	 * 分账接收方类型
	 */
	private String type;

	/**
	 * 分账接收方账号
	 */
	private String account;
}

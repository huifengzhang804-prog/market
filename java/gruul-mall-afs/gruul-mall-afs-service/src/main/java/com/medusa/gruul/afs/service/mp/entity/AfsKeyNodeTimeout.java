package com.medusa.gruul.afs.service.mp.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import io.vavr.control.Option;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * date 2022/9/3
 */

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class AfsKeyNodeTimeout {

	private static final long REQUEST_AGREE_TIMEOUT = 3 * 24 * 60 * 60;
	private static final long RETURNED_TIMEOUT = 7 * 24 * 60 * 60;
	private static final long CONFIRM_RETURNED_TIMEOUT = 7 * 24 * 60 * 60;
	/**
	 * 商家同意售后申请 超时时间 单位/秒
	 */
	private Long requestAgreeTimeout;
	/**
	 * 买家退货超时时间 单位 秒
	 */
	private Long returnedTimeout;
	/**
	 * 买家确认收货超时时间
	 */
	private Long confirmReturnedTimeout;

	@JSONField(serialize = false)
	public Long getRequestAgreeTimeoutMills() {
		return Option.of(getRequestAgreeTimeout())
				.getOrElse(REQUEST_AGREE_TIMEOUT) * 1000;
	}

	@JSONField(serialize = false)
	public Long getReturnedTimeoutMills() {
		return Option.of(getReturnedTimeout())
				.getOrElse(RETURNED_TIMEOUT) * 1000;
	}

	@JSONField(serialize = false)
	public Long getConfirmReturnedTimeoutMills() {
		return Option.of(getConfirmReturnedTimeout())
				.getOrElse(CONFIRM_RETURNED_TIMEOUT) * 1000;
	}
}

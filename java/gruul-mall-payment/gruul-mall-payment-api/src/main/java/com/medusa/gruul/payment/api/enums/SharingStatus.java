package com.medusa.gruul.payment.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 分账状态
 *
 * @author 张治保
 * date 2023/6/21
 */
@Getter
@RequiredArgsConstructor
public enum SharingStatus {
	/**
	 * 处理中
	 */
	PROCESSING(1),

	/**
	 * 已完成
	 */
	FINISHED(2),

	/**
	 * 分账出现错误
	 */
	ERROR(3),
	;

	@EnumValue
	private final Integer value;
}

package com.medusa.gruul.overview.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 分账对象 需要和
 * {@link com.medusa.gruul.payment.api.enums.ReceiverType} 保持一致
 *
 * @author 张治保
 * date 2023/6/21
 */
@Getter
@RequiredArgsConstructor
public enum SharingTarget {

	;
	@EnumValue
	private final Integer value;
}

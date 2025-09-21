package com.medusa.gruul.payment.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 分账服务方
 *
 * @author 张治保
 * date 2023/6/20
 */
@Getter
@RequiredArgsConstructor
public enum SharingService {

	/**
	 * 微信分账
	 */
	WECHAT(1),

	/**
	 * 支付宝分账
	 */
	ALI(2),

	/**
	 * 系统分账
	 */
	SYSTEM(3);

	@EnumValue
	private final Integer value;

}

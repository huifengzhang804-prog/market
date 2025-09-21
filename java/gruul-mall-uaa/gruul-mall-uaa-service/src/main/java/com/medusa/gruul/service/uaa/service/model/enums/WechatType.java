package com.medusa.gruul.service.uaa.service.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2023/5/16
 */
@Getter
@RequiredArgsConstructor
public enum WechatType {
	/**
	 * 小程序
	 */
	MA,
	/**
	 * 公众号
	 */
	MP
}

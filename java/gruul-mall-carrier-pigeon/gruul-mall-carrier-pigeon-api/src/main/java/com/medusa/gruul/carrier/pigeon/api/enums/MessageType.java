package com.medusa.gruul.carrier.pigeon.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2022/10/10
 */
@Getter
@RequiredArgsConstructor
public enum MessageType {

	/**
	 * 文本
	 */
	TEXT(1),

	/**
	 * 图片
	 */
	IMAGE(2),

	/**
	 * 商品
	 */
	PRODUCT(3),

	/**
	 * 售后
	 */
	AFS(4),

	/**
	 * 订单
	 */
	ORDER(5);


	/**
	 * 数据库里的值
	 */
	@EnumValue
	private final Integer value;
}

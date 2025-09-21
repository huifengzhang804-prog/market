package com.medusa.gruul.addon.distribute.model.vo;

import com.medusa.gruul.addon.distribute.model.enums.ShareType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/5/15
 */

@Getter
@Setter
@ToString
public class BonusShareVO implements Serializable {

	/**
	 * 佣金类型 佣金类型 1.统一设置 2.固定金额 3.百分比
	 */
	private ShareType shareType;

	/**
	 * 一级分佣
	 */
	private Long one;

	/**
	 * 二级分佣
	 */
	private Long two;

	/**
	 * 三级分佣
	 */
	private Long three;

}

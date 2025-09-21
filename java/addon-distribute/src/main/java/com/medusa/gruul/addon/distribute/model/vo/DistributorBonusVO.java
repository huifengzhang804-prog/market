package com.medusa.gruul.addon.distribute.model.vo;

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
public class DistributorBonusVO implements Serializable {

	/**
	 * 分销商用户id
	 */
	private Long userId;

	/**
	 * 分销商名称
	 */
	private String name;

	/**
	 * 电话
	 */
	private String mobile;

	/**
	 * 佣金
	 */
	private Long bonus;
}

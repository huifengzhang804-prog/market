package com.medusa.gruul.addon.distribute.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 分佣额
 *
 * @author 张治保
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Bonus {

	/**
	 * 是否分销内购
	 */
	private Boolean purchase;

	/**
	 * 一级分销商id
	 */
	private Long oneId;

	/**
	 * 一级分佣
	 */
	private long one;

	/**
	 * 二级分销商id
	 */
	private Long twoId;

	/**
	 * 一级分佣
	 */
	private long two;

	/**
	 * 三级分销商id
	 */
	private Long threeId;

	/**
	 * 一级分佣
	 */
	private long three;



}
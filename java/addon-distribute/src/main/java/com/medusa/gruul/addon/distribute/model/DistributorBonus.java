package com.medusa.gruul.addon.distribute.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 分销商 佣金 incr 数据
 *
 * @author 张治保
 * date 2023/5/11
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DistributorBonus implements Serializable {
	/**
	 * 总佣金
	 */
	private long total;

	/**
	 * 未提现佣金
	 */
	private long undrawn;

	/**
	 * 待结算佣金
	 */
	private long unsettled;

	/**
	 * 已失效佣金
	 */
	private long invalid;

}

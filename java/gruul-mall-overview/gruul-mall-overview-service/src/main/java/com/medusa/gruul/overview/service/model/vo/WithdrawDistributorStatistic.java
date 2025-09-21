package com.medusa.gruul.overview.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 提现统计
 *
 * @author 张治保
 * date 2023/5/24
 */
@Getter
@Setter
@Accessors(chain = true)
public class WithdrawDistributorStatistic implements Serializable {

	/**
	 * 累计提现到账的金额
	 */
	private Long total;

	/**
	 * 条件筛选后到账的金额
	 */
	private Long success;

	/**
	 * 条件筛选后申请中的金额
	 */
	private Long applying;

}

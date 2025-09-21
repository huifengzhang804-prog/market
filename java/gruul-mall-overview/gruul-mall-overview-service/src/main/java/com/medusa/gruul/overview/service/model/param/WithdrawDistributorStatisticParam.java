package com.medusa.gruul.overview.service.model.param;


import com.medusa.gruul.overview.api.enums.OwnerType;
import com.medusa.gruul.overview.api.enums.WithdrawSourceType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 提现统计参数
 *
 * @author 张治保
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class WithdrawDistributorStatisticParam {

	/**
	 * 拥有者id
	 */
	private Long ownerId;


	/**
	 * 提现者身份
	 */
	private OwnerType ownerType;


	/**
	 * 提现工单 来源类型
	 */
	private WithdrawSourceType withdrawSourceType;


	/**
	 * 开始时间
	 */
	private LocalDateTime startTime;

	/**
	 * 结束时间
	 */
	private LocalDateTime endTime;

}

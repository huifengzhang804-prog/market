package com.medusa.gruul.overview.service.mp.withdraw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.overview.api.entity.OverviewWithdraw;
import com.medusa.gruul.overview.service.model.dto.PlatformWithdrawQueryDTO;
import com.medusa.gruul.overview.service.model.param.WithdrawDistributorStatisticParam;
import com.medusa.gruul.overview.service.model.vo.WithdrawDistributorStatistic;

/**
 * <p>
 * 提现工单 服务类
 * </p>
 *
 * @author WuDi
 * @since 2022-11-19
 */
public interface IOverviewWithdrawService extends IService<OverviewWithdraw> {

	/**
	 * 平台分页查询提现工单
	 *
	 * @param query 查询条件
	 * @return 分页查询结果
	 */
	IPage<OverviewWithdraw> platformOrderQuery(PlatformWithdrawQueryDTO query);


	/**
	 * 查询拥有者提现工单统计
	 *
	 * @param param 查询参数
	 * @return 查询统计结果
	 */
	WithdrawDistributorStatistic distributorStatistics(WithdrawDistributorStatisticParam param);

	/**
	 * 查询店铺累计、提现中金额
	 *
	 * @param overviewWithdraw 提现工单
	 * @return Long
	 */
	Long getTotalAmountByStatus(OverviewWithdraw overviewWithdraw);
}

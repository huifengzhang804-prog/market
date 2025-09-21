package com.medusa.gruul.overview.service.mp.withdraw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.overview.api.entity.OverviewWithdraw;
import com.medusa.gruul.overview.service.model.dto.PlatformWithdrawQueryDTO;
import com.medusa.gruul.overview.service.model.param.WithdrawDistributorStatisticParam;
import com.medusa.gruul.overview.service.model.vo.WithdrawDistributorStatistic;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 提现工单 Mapper 接口
 * </p>
 *
 * @author WuDi
 * @since 2022-11-19
 */
public interface OverviewWithdrawMapper extends BaseMapper<OverviewWithdraw> {


	/**
	 * 平台分页查询提现工单
	 *
	 * @param query 查询条件
	 * @return 分页查询结果
	 */
	IPage<OverviewWithdraw> platformOrderQuery(@Param("query") PlatformWithdrawQueryDTO query);


	/**
	 * 查询拥有者提现工单统计
	 *
	 * @param param 查询参数
	 * @return 查询统计结果
	 */
	WithdrawDistributorStatistic distributorStatistics(@Param("param") WithdrawDistributorStatisticParam param);

	/**
	 * 查询店铺累计、提现中金额
	 *
	 * @param overviewWithdraw 提现工单
	 * @return Long
	 */
	Long getTotalAmountByStatus(OverviewWithdraw overviewWithdraw);
}

package com.medusa.gruul.overview.service.mp.withdraw.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.overview.api.entity.OverviewWithdraw;
import com.medusa.gruul.overview.service.model.dto.PlatformWithdrawQueryDTO;
import com.medusa.gruul.overview.service.model.param.WithdrawDistributorStatisticParam;
import com.medusa.gruul.overview.service.model.vo.WithdrawDistributorStatistic;
import com.medusa.gruul.overview.service.mp.withdraw.mapper.OverviewWithdrawMapper;
import com.medusa.gruul.overview.service.mp.withdraw.service.IOverviewWithdrawService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * <p>
 * 提现工单 服务实现类
 * </p>
 *
 * @author WuDi
 * @since 2022-11-19
 */
@Service
public class OverviewWithdrawServiceImpl extends ServiceImpl<OverviewWithdrawMapper, OverviewWithdraw> implements IOverviewWithdrawService {

	@Override
	public IPage<OverviewWithdraw> platformOrderQuery(PlatformWithdrawQueryDTO query) {
		return baseMapper.platformOrderQuery(query);
	}

	@Override
	public WithdrawDistributorStatistic distributorStatistics(WithdrawDistributorStatisticParam param) {
		return baseMapper.distributorStatistics(param);
	}

	/**
	 * 查询店铺累计、提现中金额
	 *
	 * @param overviewWithdraw 提现工单
	 * @return BigDecimal
	 */
	@Override
	public Long getTotalAmountByStatus(OverviewWithdraw overviewWithdraw) {
		return baseMapper.getTotalAmountByStatus(overviewWithdraw);
	}
}

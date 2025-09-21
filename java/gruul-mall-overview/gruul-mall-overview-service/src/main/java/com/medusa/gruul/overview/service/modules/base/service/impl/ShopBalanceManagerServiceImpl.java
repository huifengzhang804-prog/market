package com.medusa.gruul.overview.service.modules.base.service.impl;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.overview.api.entity.OverviewWithdraw;
import com.medusa.gruul.overview.api.enums.WithdrawOrderStatus;
import com.medusa.gruul.overview.service.model.OverviewConstant;
import com.medusa.gruul.overview.service.model.enums.OverviewError;
import com.medusa.gruul.overview.service.modules.base.service.ShopBalanceManagerService;
import com.medusa.gruul.overview.service.mp.base.entity.OverviewShopBalance;
import com.medusa.gruul.overview.service.mp.base.service.IOverviewShopBalanceService;
import com.medusa.gruul.overview.service.mp.withdraw.service.IOverviewWithdrawService;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 张治保
 * date 2022/11/28
 */
@Service
@RequiredArgsConstructor
public class ShopBalanceManagerServiceImpl implements ShopBalanceManagerService {
	private final IOverviewWithdrawService overviewWithdrawService;
	private final IOverviewShopBalanceService overviewShopBalanceService;
	private ShopBalanceManagerService shopBalanceManagerService;


	@Override
	@Redisson(value = OverviewConstant.NEW_SHOP_BALANCE_LOCK, key = "#shopId")
	public OverviewShopBalance getOrCreateShopBalance(Long shopId) {
		return this.getShopBalance(shopId).getOrElse(
				() -> {
					OverviewShopBalance shopBalance = this.defaultBalance(shopId);
					overviewShopBalanceService.save(shopBalance);
					return shopBalance;
				}
		);
	}

	/**
	 * 店铺结算 余额、累计提现、提现中
	 *
	 * @param shopId 店铺id
	 * @return OverviewShopBalance<un drawn, withdrawalTotal, withdrawing>
	 */
	@Override
	public OverviewShopBalance get(Long shopId) {
		OverviewShopBalance orCreateShopBalance = shopBalanceManagerService.getOrCreateShopBalance(shopId);

		OverviewWithdraw overviewWithdraw = new OverviewWithdraw();
		overviewWithdraw.setOwnerId(shopId);
		overviewWithdraw.setStatus(WithdrawOrderStatus.APPLYING);
		//提现中
		Long withdrawing = overviewWithdrawService.getTotalAmountByStatus(overviewWithdraw);
		orCreateShopBalance.setWithdrawing(withdrawing);
		//累计提现
		overviewWithdraw.setStatus(WithdrawOrderStatus.SUCCESS);
		Long withdrawalTotal = overviewWithdrawService.getTotalAmountByStatus(overviewWithdraw);
		orCreateShopBalance.setWithdrawalTotal(withdrawalTotal);
		return orCreateShopBalance;
	}


	/**
	 * 1。 售后关闭 减去 售后退款金额
	 * 2。 订单完成减去待退款金额
	 * 3。 订单支付 加上待退款金额
	 *
	 * @param shopId 店铺id
	 * @param amount 未结算余额
	 */
	@Override
	@Redisson(value = OverviewConstant.SHOP_BALANCE_UNCOMPLETED_UPDATE_LOCK, key = "#shopId")
	public void uncompletedBalanceIncrement(Long shopId, Long amount) {
		shopBalanceManagerService.getOrCreateShopBalance(shopId);
		boolean success = overviewShopBalanceService.lambdaUpdate()
				.eq(OverviewShopBalance::getShopId, shopId)
				.setSql(StrUtil.format(OverviewConstant.UPDATE_BALANCE_TOTAL_SQL_TEMPLATE, amount))
				.setSql(StrUtil.format(OverviewConstant.UPDATE_BALANCE_UNDRAWN_SQL_TEMPLATE, amount))
				.update();
		SystemCode.DATA_UPDATE_FAILED.falseThrow(success);
	}

	@Override
	@Redisson(value = OverviewConstant.SHOP_BALANCE_UNDRAWN_UPDATE_LOCK, key = "#shopId")
	public void undrawnBalanceDecrement(Long shopId, Long amount) {
		OverviewShopBalance shopBalance = shopBalanceManagerService.getOrCreateShopBalance(shopId);
		Long undrawn = shopBalance.getUndrawn();
		OverviewError.INSUFFICIENT_BALANCE.trueThrow(amount > undrawn);
		boolean success = overviewShopBalanceService.lambdaUpdate()
				.eq(OverviewShopBalance::getShopId, shopId)
				.setSql(StrUtil.format(OverviewConstant.UPDATE_BALANCE_UNDRAWN_SQL_TEMPLATE, -amount))
				.update();
		SystemCode.DATA_UPDATE_FAILED.falseThrow(success);
	}


	@Override
	@Redisson(value = OverviewConstant.SHOP_BALANCE_UNDRAWN_UPDATE_LOCK, key = "#shopId")
	public void undrawnBalanceIncrement(boolean updateTotal, Long shopId, Long amount) {
		if (amount == 0L) {
			return;
		}
		shopBalanceManagerService.getOrCreateShopBalance(shopId);
		boolean success = overviewShopBalanceService.lambdaUpdate()
				.setSql(updateTotal, StrUtil.format(OverviewConstant.UPDATE_BALANCE_TOTAL_SQL_TEMPLATE, amount))
				.setSql(StrUtil.format(OverviewConstant.UPDATE_BALANCE_UNDRAWN_SQL_TEMPLATE, amount))
				.eq(OverviewShopBalance::getShopId, shopId)
				.update();
		SystemCode.DATA_UPDATE_FAILED.falseThrow(success);
	}

	@Override
	public void orderCompleted(Long shopId, Long amount) {
		shopBalanceManagerService.undrawnBalanceIncrement(Boolean.TRUE, shopId, amount);
	}

	/**
	 * 更新店铺余额
	 *
	 * @param shopId 店铺id
	 * @param amount 金额
	 */
	@Override
	public void updateTotalByShopId(Long shopId, Long amount) {
		if (amount == 0L) {
			return;
		}
		shopBalanceManagerService.getOrCreateShopBalance(shopId);
		boolean success = overviewShopBalanceService.lambdaUpdate()
				.setSql(StrUtil.format(OverviewConstant.UPDATE_BALANCE_TOTAL_SQL_TEMPLATE, amount))
				.eq(OverviewShopBalance::getShopId, shopId)
				.update();
		SystemCode.DATA_UPDATE_FAILED.falseThrow(success);
	}


	private Option<OverviewShopBalance> getShopBalance(Long shopId) {
		return Option.of(overviewShopBalanceService.lambdaQuery().eq(OverviewShopBalance::getShopId, shopId).one());
	}

	private OverviewShopBalance defaultBalance(Long shopId) {
		return new OverviewShopBalance()
				.setShopId(shopId)
				.setTotal(0L)
				.setUndrawn(0L)
				.setUncompleted(0L);
	}

	@Autowired
	public void setShopBalanceManagerService(ShopBalanceManagerService shopBalanceManagerService) {
		this.shopBalanceManagerService = shopBalanceManagerService;
	}


}

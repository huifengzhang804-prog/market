package com.medusa.gruul.payment.service.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.payment.api.entity.PaymentHistory;
import com.medusa.gruul.payment.api.enums.DealType;
import com.medusa.gruul.payment.api.model.param.SavingsOrderHistoryParam;
import com.medusa.gruul.payment.api.model.param.UserPaymentHistoryParam;
import com.medusa.gruul.payment.api.model.vo.SavingsOrderHistoryVO;
import com.medusa.gruul.payment.api.model.vo.UserPaymentHistoryStatisticsVO;
import com.medusa.gruul.payment.api.model.vo.UserPaymentHistoryVO;
import com.medusa.gruul.payment.service.model.vo.BalanceBillingVO;
import com.medusa.gruul.payment.service.mp.service.IPaymentHistoryService;
import com.medusa.gruul.payment.service.service.UserPaymentHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaoq
 * @Description
 * @date 2022-09-28 14:23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserPaymentHistoryServiceImpl implements UserPaymentHistoryService {
	private final IPaymentHistoryService paymentHistoryService;

	/**
	 * 查询用户交易历史信息
	 *
	 * @param paymentHistoryParam 查询 param
	 * @return Page<用户支付历史VO>
	 */
	@Override
	public Page<UserPaymentHistoryVO> queryUserPaymentHistory(UserPaymentHistoryParam paymentHistoryParam) {

		List<DealType> dealType = new ArrayList<>(CommonPool.NUMBER_EIGHT);
		//交易状态
		switch (paymentHistoryParam.getDealAggregationType()) {
			case CHARGING -> {
				dealType.add(DealType.SYSTEM_GIVE);
				dealType.add(DealType.PERSONAL_CHARGING);
				dealType.add(DealType.SYSTEM_CHARGING);
			}
			case USER_CONSUME -> {
				dealType.add(DealType.SHOPPING_PURCHASE);
				dealType.add(DealType.PURCHASE_MEMBER);
			}
			case REFUND_SUCCEED -> dealType.add(DealType.REFUND_SUCCEED);
			default -> {
			}
		}
		//临时存储
		long size = paymentHistoryParam.getSize();
		//查询
		paymentHistoryParam.setSize(-1);
		List<UserPaymentHistoryStatisticsVO> userPaymentHistoryStatisticsList
				= paymentHistoryService.queryUserPaymentHistoryStatisticsByParam(paymentHistoryParam, dealType);

		paymentHistoryParam.setSize(size);
		Page<UserPaymentHistoryVO> userPaymentHistoryPage = paymentHistoryService.queryUserPaymentHistoryByParam(paymentHistoryParam, dealType);
		paymentHistoryParam.setUserPaymentHistoryStatisticsList(userPaymentHistoryStatisticsList);
		return userPaymentHistoryPage;
	}

	@Override
	public void removeUserPaymentHistory(Long id) {
		LambdaUpdateChainWrapper<PaymentHistory> update = paymentHistoryService.lambdaUpdate().eq(PaymentHistory::getId, id);
		ISecurity.match()
				.ifUser(secureUser -> update.eq(PaymentHistory::getUserId, secureUser.getId()));
		boolean remove = update.remove();
		SystemCode.DATA_DELETE_FAILED.falseThrow(remove);
	}

	/**
	 * 获取用户储蓄信息
	 *
	 * @param userId 用户id
	 * @return BalanceBillingVO.java
	 */
	@Override
	public BalanceBillingVO getUserSavingsIno(Long userId) {
		BalanceBillingVO billingVO = new BalanceBillingVO();
		// 获取用户充值次数
		Long count = this.paymentHistoryService.lambdaQuery()
				.eq(PaymentHistory::getUserId, userId)
				.eq(PaymentHistory::getChangeType, ChangeType.INCREASE)
				.eq(PaymentHistory::getDealType, DealType.PERSONAL_CHARGING).count();
		Long userTotalReapMoney = paymentHistoryService.getUserTotalReapMoney(userId);
		billingVO.setTotalMoney(userTotalReapMoney != null ? userTotalReapMoney : CommonPool.NUMBER_ZERO);
		billingVO.setRechargeNumber(count);
		return billingVO;
	}

	/**
	 * 储值订单列表信息
	 *
	 * @param savingsOrderHistoryParam 储值订单查询param
	 */
	@Override
	public IPage<SavingsOrderHistoryVO> getSavingsOrderList(SavingsOrderHistoryParam savingsOrderHistoryParam) {
		return paymentHistoryService.getSavingsOrderList(savingsOrderHistoryParam);

	}
}

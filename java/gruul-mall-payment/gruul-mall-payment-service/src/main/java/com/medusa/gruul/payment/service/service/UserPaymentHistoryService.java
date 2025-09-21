package com.medusa.gruul.payment.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.payment.api.model.param.SavingsOrderHistoryParam;
import com.medusa.gruul.payment.api.model.param.UserPaymentHistoryParam;
import com.medusa.gruul.payment.api.model.vo.SavingsOrderHistoryVO;
import com.medusa.gruul.payment.api.model.vo.UserPaymentHistoryVO;
import com.medusa.gruul.payment.service.model.vo.BalanceBillingVO;

/**
 * 用户支付历史记录
 *
 * @author xiaoq
 * @Description UserPaymentHistoryService.java
 * @date 2022-09-28 14:22
 */
public interface UserPaymentHistoryService {
	/**
	 * 获取用户余额支付历史
	 *
	 * @param paymentHistoryParam 查询param
	 * @return Page<UserPaymentHistoryVO>
	 */
	Page<UserPaymentHistoryVO> queryUserPaymentHistory(UserPaymentHistoryParam paymentHistoryParam);

	/**
	 * 用户余额明细记录删除
	 *
	 * @param id 余额明细id
	 */
	void removeUserPaymentHistory(Long id);

	/**
	 * 获取用户储蓄信息
	 *
	 * @param userId 用户id
	 * @return BalanceBillingVO
	 */
	BalanceBillingVO getUserSavingsIno(Long userId);

	/**
	 * 储值订单信息
	 *
	 * @param savingsOrderHistoryParam 储值订单查询param
	 * @return IPage<储值订单历史vo>
	 */
	IPage<SavingsOrderHistoryVO> getSavingsOrderList(SavingsOrderHistoryParam savingsOrderHistoryParam);
}

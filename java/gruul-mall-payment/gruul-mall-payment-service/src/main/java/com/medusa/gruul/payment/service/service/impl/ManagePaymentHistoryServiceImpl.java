package com.medusa.gruul.payment.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.message.DataChangeMessage;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.payment.api.entity.PaymentHistory;
import com.medusa.gruul.payment.api.enums.DealType;
import com.medusa.gruul.payment.api.model.dto.SavingsOrderRemarkDTO;
import com.medusa.gruul.payment.service.mp.service.IPaymentHistoryService;
import com.medusa.gruul.payment.service.service.ManagePaymentHistoryService;
import com.medusa.gruul.user.api.model.dto.BalanceChangeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author xiaoq
 * @Description ManagePaymentHistoryServiceImpl.java
 * @date 2022-12-22 18:47
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ManagePaymentHistoryServiceImpl implements ManagePaymentHistoryService {
	private final IPaymentHistoryService paymentHistoryService;


	/**
	 * 用户余额明细生成(商户/个人操作)
	 *
	 * @param balanceChange 余额变化消息体
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void createBalanceDetail(BalanceChangeDTO balanceChange) {
		PaymentHistory paymentHistory = new PaymentHistory();
		if (balanceChange.getPayType() != null) {
			DataChangeMessage personDataChangeMessage = balanceChange.getPersonDataChangeMessage();
			paymentHistory
					.setChangeType(personDataChangeMessage.getChangeType())
					.setUserId(personDataChangeMessage.getUserId())
					.setPayType(balanceChange.getPayType())
					.setUserName(balanceChange.getUserNikeName())
					.setUserHeadPortrait(balanceChange.getUserHeadPortrait())
					.setMoney(personDataChangeMessage.getValue())
					.setDealType(DealType.valueOf(personDataChangeMessage.getExtendInfo()));
			this.paymentHistoryService.save(paymentHistory);
		}
		if (balanceChange.getSystemDataChangeMessage() != null) {
			DataChangeMessage systemDataChangeMessage = balanceChange.getSystemDataChangeMessage();
			paymentHistory = new PaymentHistory();
			paymentHistory
					.setChangeType(systemDataChangeMessage.getChangeType())
					.setUserId(systemDataChangeMessage.getUserId())
					.setMoney(systemDataChangeMessage.getValue())
					.setUserName(balanceChange.getUserNikeName())
					.setUserHeadPortrait(balanceChange.getUserHeadPortrait())
					.setDealType(DealType.valueOf(systemDataChangeMessage.getExtendInfo()));
			this.paymentHistoryService.save(paymentHistory);
		}
	}

	/**
	 * 储值订单批量备注
	 *
	 * @param savingsOrderRemark 储值订单备注信息
	 */
	@Override
	public void savingsOrderBatchRemark(SavingsOrderRemarkDTO savingsOrderRemark) {
		String remark = StrUtil.trim(savingsOrderRemark.getRemark());
		boolean update = paymentHistoryService.lambdaUpdate().set(PaymentHistory::getRemark, remark).in(BaseEntity::getId, savingsOrderRemark.getIds()).update();
		SystemCode.DATA_UPDATE_FAILED.falseThrow(update);
	}

}

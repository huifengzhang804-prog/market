package com.medusa.gruul.payment.service.service;


import com.medusa.gruul.payment.api.model.dto.SavingsOrderRemarkDTO;
import com.medusa.gruul.user.api.model.dto.BalanceChangeDTO;

/**
 * 用户支付历史记录(管理台)
 *
 * @author xiaoq
 * @Description ManagePaymentHistoryService.java
 * @date 2022-12-22 14:22
 */
public interface ManagePaymentHistoryService {

    /**
     * 用户余额明细记录生成
     *
     * @param balanceChange 余额变化消息体
     */
    void createBalanceDetail(BalanceChangeDTO balanceChange);

    /**
     * 储值订单批量备注
     *
     * @param savingsOrderRemark 储值订单备注信息
     */
    void savingsOrderBatchRemark(SavingsOrderRemarkDTO savingsOrderRemark);
}

package com.medusa.gruul.addon.rebate.service;

import com.medusa.gruul.addon.rebate.model.dto.WithdrawDTO;
import com.medusa.gruul.addon.rebate.model.enums.RebateType;
import com.medusa.gruul.addon.rebate.model.vo.RebatePayDTO;
import com.medusa.gruul.addon.rebate.model.vo.RebateTransactionsVO;
import com.medusa.gruul.addon.rebate.mp.entity.RebateTransactions;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.goods.api.model.dto.EstimateRebateDTO;
import jakarta.annotation.Nullable;

/**
 * @author jinbu
 */
public interface RebateTransactionsHandleService {

    /**
     * 商品详情查询返利金额
     *
     * @param userId   基于计算的用户 id 如果为空则取最大值计算
     * @param estimate 商品金额与服务费比例
     * @return 商品返利金额
     */
    Long getProductRebateAmount(@Nullable Long userId, EstimateRebateDTO estimate);


    /**
     * 获取可支付消费返利余额
     *
     * @param orderNo   订单号
     * @param payAmount 支付金额
     * @return 可支付消费返利余额
     */
    RebatePayDTO getCanRebateBalance(String orderNo, Long payAmount);

    /**
     * 获取用户消费返利相关余额
     *
     * @return 用户消费返利余额
     */
    RebateTransactionsVO getRebateTransactions();


    /**
     * 操作返利金额并生成操作明细
     *
     * @param balance    返利余额调整数据
     * @param rebateType 返利类型
     * @param desc       明细描述
     */
    void updateRebateBalance(RebateTransactions balance, RebateType rebateType, String desc);


    /**
     * 消费返利提现
     *
     * @param secureUser 用户信息
     * @param withdraw   提现方式
     */
    void createWithdrawOrder(SecureUser<?> secureUser, WithdrawDTO withdraw);


    /**
     * 消费返利是否已启用
     *
     * @return 是否启用
     */
    Boolean enabled();

}

package com.medusa.gruul.payment.service.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.payment.api.entity.PaymentHistory;
import com.medusa.gruul.payment.api.enums.DealType;
import com.medusa.gruul.payment.api.model.param.SavingsOrderHistoryParam;
import com.medusa.gruul.payment.api.model.param.UserPaymentHistoryParam;
import com.medusa.gruul.payment.api.model.vo.SavingsOrderHistoryVO;
import com.medusa.gruul.payment.api.model.vo.UserPaymentHistoryStatisticsVO;
import com.medusa.gruul.payment.api.model.vo.UserPaymentHistoryVO;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2022-09-28 14:25
 */
public interface IPaymentHistoryService extends IService<PaymentHistory> {
    /**
     * 获取用户交易历史详情 by param
     *
     * @param paymentHistoryParam 查询param
     * @param dealType            交易类别
     * @return Page<UserPaymentHistoryVO>
     */
    Page<UserPaymentHistoryVO> queryUserPaymentHistoryByParam(UserPaymentHistoryParam paymentHistoryParam, List<DealType> dealType);

    /**
     * 获取用户交易统计数据 by param
     *
     * @param paymentHistoryParam 查询param
     * @param dealType            交易类别
     * @return List<UserPaymentHistoryStatisticsVO>
     */
    List<UserPaymentHistoryStatisticsVO> queryUserPaymentHistoryStatisticsByParam(UserPaymentHistoryParam paymentHistoryParam, List<DealType> dealType);

    /**
     * 获取用户合计金额
     *
     * @param userId 用户id
     * @return 合计金额(充值+赠送)
     */
    Long getUserTotalReapMoney(Long userId);

    /**
     * 储值订单信息
     *
     * @param savingsOrderHistoryParam 查询param
     * @return IPage<SavingsOrderHistoryVO>
     */
    IPage<SavingsOrderHistoryVO> getSavingsOrderList(SavingsOrderHistoryParam savingsOrderHistoryParam);
}

package com.medusa.gruul.payment.service.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.payment.api.entity.PaymentHistory;
import com.medusa.gruul.payment.api.enums.DealType;
import com.medusa.gruul.payment.api.model.param.SavingsOrderHistoryParam;
import com.medusa.gruul.payment.api.model.param.UserPaymentHistoryParam;
import com.medusa.gruul.payment.api.model.vo.SavingsOrderHistoryVO;
import com.medusa.gruul.payment.api.model.vo.UserPaymentHistoryStatisticsVO;
import com.medusa.gruul.payment.api.model.vo.UserPaymentHistoryVO;
import com.medusa.gruul.payment.service.mp.mapper.PaymentHistoryMapper;
import com.medusa.gruul.payment.service.mp.service.IPaymentHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2022-09-28 14:26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentHistoryServiceImpl extends ServiceImpl<PaymentHistoryMapper, PaymentHistory> implements IPaymentHistoryService {


    @Override
    public Page<UserPaymentHistoryVO> queryUserPaymentHistoryByParam(UserPaymentHistoryParam paymentHistoryParam, List<DealType> dealType) {
        return this.baseMapper.queryUserPaymentHistoryByParam(paymentHistoryParam, dealType);
    }

    @Override
    public List<UserPaymentHistoryStatisticsVO> queryUserPaymentHistoryStatisticsByParam(UserPaymentHistoryParam paymentHistoryParam, List<DealType> dealType) {
        return this.baseMapper.queryUserPaymentHistoryStatisticsByParam(paymentHistoryParam, dealType);
    }

    @Override
    public Long getUserTotalReapMoney(Long userId) {
        return this.baseMapper.queryUserTotalReapMoney(userId);

    }

    @Override
    public IPage<SavingsOrderHistoryVO> getSavingsOrderList(SavingsOrderHistoryParam savingsOrderHistoryParam) {
        return this.baseMapper.getSavingsOrderList(savingsOrderHistoryParam);
    }
}

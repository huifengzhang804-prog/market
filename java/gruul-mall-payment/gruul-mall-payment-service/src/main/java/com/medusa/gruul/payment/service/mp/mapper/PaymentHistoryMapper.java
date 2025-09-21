package com.medusa.gruul.payment.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.payment.api.entity.PaymentHistory;
import com.medusa.gruul.payment.api.enums.DealType;
import com.medusa.gruul.payment.api.model.param.SavingsOrderHistoryParam;
import com.medusa.gruul.payment.api.model.param.UserPaymentHistoryParam;
import com.medusa.gruul.payment.api.model.vo.SavingsOrderHistoryVO;
import com.medusa.gruul.payment.api.model.vo.UserPaymentHistoryStatisticsVO;
import com.medusa.gruul.payment.api.model.vo.UserPaymentHistoryVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2022-09-28 14:27
 */
public interface PaymentHistoryMapper extends BaseMapper<PaymentHistory> {


    /**
     * 获取用户支付历史VO by param
     *
     * @param paymentHistoryParam 查询param
     * @param dealType            查询类别
     * @return page<用户支付历史VO>
     */
    Page<UserPaymentHistoryVO> queryUserPaymentHistoryByParam(@Param("paymentHistoryParam") UserPaymentHistoryParam paymentHistoryParam, @Param("dealTypeList") List<DealType> dealType);

    /**
     * 获取用户支付历史统计数据 param
     *
     * @param paymentHistoryParam 查询param
     * @param dealType 查询类别
     * @return List<UserPaymentHistoryStatisticsVO>
     */
    List<UserPaymentHistoryStatisticsVO> queryUserPaymentHistoryStatisticsByParam(@Param("paymentHistoryParam") UserPaymentHistoryParam paymentHistoryParam, @Param("dealTypeList") List<DealType> dealType);

    /**
     * 获取用户累计获得金额
     *
     * @param userId 用户id
     * @return 累计金额(充值+赠送)
     */
    Long queryUserTotalReapMoney(@Param("userId") Long userId);

    /**
     *  储值订单
     *
     * @param savingsOrderHistoryParam param
     * @return  IPage<SavingsOrderHistoryVO>
     */
    IPage<SavingsOrderHistoryVO> getSavingsOrderList(@Param("savingsOrderHistoryParam") SavingsOrderHistoryParam savingsOrderHistoryParam);
}



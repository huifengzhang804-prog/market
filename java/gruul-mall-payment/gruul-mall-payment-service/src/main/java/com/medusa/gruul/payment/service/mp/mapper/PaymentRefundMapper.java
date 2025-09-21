package com.medusa.gruul.payment.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.payment.api.entity.PaymentRefund;
import org.apache.ibatis.annotations.Param;

/**
 *
 *
 *
 *
 * @author xiaoq
 * @Description
 * @date 2022-08-08 11:14
 */
public interface PaymentRefundMapper extends BaseMapper<PaymentRefund> {
    /**
     *  获取业务订单号退款总和
     *
     * @param orderNum 业务订单号
     * @return 业务订单号退款总和
     */
    Long querySumRefundAmount(@Param("orderNum") String orderNum);
}

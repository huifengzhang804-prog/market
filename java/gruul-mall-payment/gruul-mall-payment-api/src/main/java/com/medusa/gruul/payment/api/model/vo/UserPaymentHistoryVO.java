package com.medusa.gruul.payment.api.model.vo;

import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.payment.api.enums.DealType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户支付历史VO
 *
 * @author xiaoq
 * @Description UserPaymentHistoryVO.java
 * @date 2022-09-28 15:44
 */
@Data
public class UserPaymentHistoryVO {

    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 商户隔离id
     */
    private Long shopId;

    /**
     * 金额
     */
    private Long money;


    /**
     * 交易类型
     */
    private DealType dealType;


    /**
     * 变化类型
     */
    private ChangeType changeType;


    /**
     * 支付渠道
     */
    private PayType payType;


    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}

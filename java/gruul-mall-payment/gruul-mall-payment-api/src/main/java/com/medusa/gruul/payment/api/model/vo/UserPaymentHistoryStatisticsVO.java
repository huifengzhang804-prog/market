package com.medusa.gruul.payment.api.model.vo;

import com.medusa.gruul.common.model.enums.ChangeType;
import lombok.Data;

/**
 * 用户支付历史统计VO
 *
 * @author xiaoq
 * @Description UserPaymentHistoryStatisticsVO.java
 * @date 2022-10-13 14:53
 */
@Data
public class UserPaymentHistoryStatisticsVO {

    /**
     * 变化类型
     */
    private ChangeType changeType;


    /**
     * 统计金额
     */
    private Long statisticsMoney;
}

package com.medusa.gruul.payment.api.model.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.payment.api.enums.DealAggregationType;
import com.medusa.gruul.payment.api.model.vo.UserPaymentHistoryStatisticsVO;
import lombok.Data;

import java.util.List;

/**
 * 用户支付历史param
 *
 * @author xiaoq
 * @ Description UserPaymentHistoryParam.java
 * @date 2022-09-28 14:50
 */
@Data
public class UserPaymentHistoryParam extends Page<Object> {

    /**
     * 变化类型
     */
    private ChangeType changeType;

    /**
     * 查询交易聚合类型
     */
    private DealAggregationType dealAggregationType;


    /**
     * 查询时间
     */
    private String queryTime;

    /**
     * 开始时间
     */
    private String leftQueryTime;

    /**
     * 结束时间
     */
    private String rightQueryTime;

    /**
     * 用户id
     */
    private Long userId;


    /**
     * 统计List
     */
    private List<UserPaymentHistoryStatisticsVO> userPaymentHistoryStatisticsList;



}

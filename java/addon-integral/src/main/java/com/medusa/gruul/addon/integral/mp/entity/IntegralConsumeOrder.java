package com.medusa.gruul.addon.integral.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author miskw
 * @date 2023/7/18
 * @describe 用户订单消费记录
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_integral_consume_order")
public class IntegralConsumeOrder extends BaseEntity {
    /**
     * 订单交易数量
     */
    private Integer dealQuantityCount;
    /**
     * 订单交易金额
     */
    private Long dealMoneyCount;
    /**
     * 用户id
     */
    private Long userId;
}

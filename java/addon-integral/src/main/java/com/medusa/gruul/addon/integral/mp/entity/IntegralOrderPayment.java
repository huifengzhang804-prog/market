package com.medusa.gruul.addon.integral.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 积分订单支付表
 *
 * @author xiaoq
 * @Description 积分订单支付表
 * @date 2023-02-01 14:09
 */

@Getter
@Setter
@Accessors(chain = true)
@TableName("t_integral_order_payment")
public class IntegralOrderPayment extends BaseEntity {
    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付流水号
     */
    private String sn;

    /**
     * 支付用户id
     */
    private Long payerId;


    /**
     * 微信用户openId
     */
    private String openId;


    /**
     * 支付渠道
     */
    private PayType payType;

    /**
     * 第二种支付方式
     */
    private PayType secPayType;

    /**
     * 运费
     */
    private Long freightAmount;

    /**
     * 总积分
     */
    private Long totalIntegral;

    /**
     * 支付积分
     */
    private Long payIntegral;
    /**
     * 非积分支付的金额
     */
    private Long salePrice;

    /**
     * 支付总金额
     */
    private Long payAmount;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

}

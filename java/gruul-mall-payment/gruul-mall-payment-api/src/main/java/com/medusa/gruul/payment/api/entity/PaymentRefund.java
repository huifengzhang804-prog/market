package com.medusa.gruul.payment.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * 支付退款表信息
 *
 *
 * @author xiaoq
 * @ Description PaymentRefund.java
 * @date 2022-08-01 16:28
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_payment_refund")
public class PaymentRefund extends BaseEntity {

    /**
     * 业务订单号
     */
    private String orderNum;

    /**
     * 内部退款业务订单号
     */
    private String afsNum;

    /**
     * 外部退款单号
     */
    private String refundNo;

    /**
     * 商铺id
     */
    private Long shopId;


    /**
     * 同步请求参数
     */
    @TableField(value = "asyn_request")
    private String asynRequest;


    /**
     * 同步返回结果
     */
    @TableField("asyn_result")
    private String asynResult;


    /**
     * 异步回调数据
     */
    @TableField("syn_callback")
    private String synCallback;


    /**
     * 路由键
     */
    @TableField("route_key")
    private String routeKey;


    /**
     * 交换机
     */
    private String exchange;
}

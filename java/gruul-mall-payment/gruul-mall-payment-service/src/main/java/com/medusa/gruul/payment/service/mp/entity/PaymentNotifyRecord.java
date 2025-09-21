package com.medusa.gruul.payment.service.mp.entity;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.github.binarywang.wxpay.bean.ecommerce.CombineTransactionsResult;
import com.medusa.gruul.payment.api.enums.PayOrderType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 张治保
 * date 2023/6/7
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName(value = "t_payment_notify_record", autoResultMap = true)
public class PaymentNotifyRecord implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * appid
     */
    private String appid;

    /**
     * 支付发起方 商户号
     */
    private String mchId;

    /**
     * 支付订单类型
     */
    private PayOrderType orderType;

    /**
     * 主订单号
     */
    private String orderNo;

    /**
     * 子订单号
     */
    private String subOrderNo;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 支付流水号
     */
    private String transactionId;

    /**
     * 支付通知结果
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private CombineTransactionsResult.SubOrders notify;

    /**
     * 额外参数 json
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private JSONObject extra;

    /**
     * 支付通知时间
     */
    private LocalDateTime notifyTime;
}

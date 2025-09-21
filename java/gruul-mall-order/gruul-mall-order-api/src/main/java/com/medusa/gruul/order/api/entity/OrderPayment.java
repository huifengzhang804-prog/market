package com.medusa.gruul.order.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.model.pay.Transaction;
import com.medusa.gruul.common.mp.handler.type.MapToKeyValuesTypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 订单支付信息
 *
 * @author 张治保
 * @since 2022-06-08
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
@TableName(value = "t_order_payment", autoResultMap = true)
public class OrderPayment extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付的用户id
     */
    private Long payerId;

    /**
     * 各店铺支付流水信息
     */
    @TableField(typeHandler = MapToKeyValuesTypeHandler.class)
    private Map<Long, Transaction> transactions;

    /**
     * 支付类型
     */
    private PayType type;

    /**
     * 订单总金额
     */
    private Long totalAmount;

    /**
     * 总运费
     */
    private Long freightAmount;

    /**
     * 优惠总金额
     */
    private Long discountAmount;

    /**
     * 支付总金额金额 = 订单总金额 + 运费 - 优惠总金额
     */
    private Long payAmount;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 支付额外字段
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private PaymentExtra extra;

    /**
     * 关联订单信息
     */
    @ToString.Exclude
    @TableField(exist = false)
    private Order order;


    /**
     * 包裹金额
     */
    @TableField(exist = false)
    private Long packageAmount;


    public PaymentExtra getExtra() {
        return extra == null ? new PaymentExtra() : extra;
    }

    public Transaction transaction(Long shopId) {
        Map<Long, Transaction> transactions = getTransactions();
        Transaction transaction = transactions.get(shopId);
        return transaction == null ? transactions.get(0L) : transaction;
    }


    @Getter
    @Setter
    @ToString
    @Accessors(chain = true)
    public static class PaymentExtra implements Serializable {
        /**
         * 是否已使用了返利支付
         */
        private Boolean rebatePay = Boolean.FALSE;
    }


}

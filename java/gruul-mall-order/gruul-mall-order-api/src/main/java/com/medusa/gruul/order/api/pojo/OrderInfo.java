package com.medusa.gruul.order.api.pojo;

import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.model.pay.Transaction;
import com.medusa.gruul.order.api.enums.OrderCloseType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 张治保
 * date 2022/6/21
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderInfo implements Serializable {
    private static final long serialVersionUID = -5108496409464796118L;

    /**
     * 关闭类型  只有关闭订单时不为空
     */
    private OrderCloseType closeType;

    /**
     * 活动类型
     */
    private OrderType activityType;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 订单号
     */
    @NotNull
    private String orderNo;

    /**
     * 买家id
     */
    @NotNull
    private Long buyerId;

    /**
     * 店铺id 操作的是店铺里面的商品时
     */
    private Long shopId;

    /**
     * 锁定库存信息
     */
    private List<SkuStock> skuStocks;

    /**
     * 关闭时间
     */
    private LocalDateTime closeTime;

    /**
     * 售后，
     */
    private Afs afs;

    /**
     * 是否还有其它未完成的订单
     */
    private Boolean haveUncompletedItem;

    /**
     * 交易流水信息
     */
    private Transaction transaction;


    public Transaction transaction() {
        return transaction == null ? new Transaction() : transaction;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Afs {

        /**
         * 如果是售后 包含售后工单号
         */
        private String afsNo;

        /**
         * 售后工单 退款交易流水号
         */
        private String afsTradeNo;

        /**
         * 退款金额
         */
        private Long refundAmount;

        /**
         * 包裹id
         */
        private Long packageId;

        /**
         * 订单商品项id
         */
        private Long shopOrderItemId;

        /**
         * 商品sku 规格 信息
         */
        private List<String> specs;


    }
}

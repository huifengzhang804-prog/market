package com.medusa.gruul.order.api.model;

import cn.hutool.json.JSONObject;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.pay.Transaction;
import com.medusa.gruul.order.api.entity.OrderDiscount;
import com.medusa.gruul.order.api.entity.OrderEvaluate;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import io.vavr.control.Option;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 订单完成
 *
 * @author WuDi
 * date 2022/10/31
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderCompletedDTO implements Serializable {

    /**
     * 配送方式
     */
    private DistributionMode distributionMode;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 支付总金额
     */
    private Long totalAmount;

    /**
     * 交易流水信息
     */
    private Transaction transaction;

    /**
     * 是否还有其它未完成的订单
     */
    private Boolean haveUncompletedItem;


    /**
     * 附加数据
     */
    private JSONObject extra;
    /**
     * 评价后的订单商品列表
     */
    private List<ShopOrderItem> shopOrderItems;

    /**
     * 订单评论
     */
    private List<OrderEvaluate> orderEvaluates;


    /**
     * 订单优惠项
     */
    private List<OrderDiscount> orderDiscounts;


    /**
     * 获取交易流水号
     *
     * @return 交易流水号
     */
    public Transaction transaction() {
        return Option.of(transaction).getOrElse(Transaction::new);
    }

}

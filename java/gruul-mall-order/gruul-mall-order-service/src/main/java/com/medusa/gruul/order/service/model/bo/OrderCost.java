package com.medusa.gruul.order.service.model.bo;

import com.medusa.gruul.order.api.entity.OrderDiscountItem;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author 张治保
 * date 2022/10/22
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderCost {

    /**
     * 总折扣价
     */
    private long totalDiscountAmount;

    /**
     * 总金额
     */
    private long totalAmount;

    /**
     * 总运费
     */
    private long totalFreightAmount;


    /**
     * 当前订单对应的 订单商品项
     */
    private List<ShopOrderItem> shopOrderItems;

    /**
     * 当前订单对应的 订单折扣项
     */
    private List<OrderDiscountItem> orderDiscountItems;


    /**
     * 增加总折扣价
     *
     * @param incrAmount 增加的金额
     * @return this
     */
    public OrderCost incrTotalDiscountAmount(long incrAmount) {
        this.totalDiscountAmount += incrAmount;
        return this;
    }

    /**
     * 增加总金额
     *
     * @param incrAmount 增加的金额
     * @return this
     */
    public OrderCost incrTotalAmount(long incrAmount) {
        this.totalAmount += incrAmount;
        return this;
    }

    /**
     * 增加总运费
     *
     * @param incrAmount 增加的金额
     * @return this
     */
    public OrderCost incrTotalFreightAmount(long incrAmount) {
        this.totalFreightAmount += incrAmount;
        return this;
    }

}

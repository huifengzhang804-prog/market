package com.medusa.gruul.order.api.addon.rebate;

import com.medusa.gruul.order.api.entity.ShopOrderItem;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 返利支付参数
 *
 * @author 张治保
 * date 2023/9/13
 */

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class RebatePayParam implements Serializable {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 订单支付商品项
     */
    private List<ShopOrderItem> orderItems;

}

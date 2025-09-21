package com.medusa.gruul.order.service.model.vo;

import com.medusa.gruul.order.api.entity.Order;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author: wufuzhong
 * @Date: 2023/11/10 11:39:54
 * @Description: 平台发货订单对象 VO
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderPlatFormDeliveryVO {

    /**
     * 自营店铺订单
     */
    private List<Order> shopOrders;

    /**
     * 自营供应商订单
     */
    private List<Order> supplierOrders;
}

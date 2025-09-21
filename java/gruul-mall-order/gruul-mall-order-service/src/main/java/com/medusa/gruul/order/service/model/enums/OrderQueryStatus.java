package com.medusa.gruul.order.service.model.enums;

import com.medusa.gruul.order.api.enums.OrderStatus;
import com.medusa.gruul.order.api.enums.PackageStatus;
import com.medusa.gruul.order.api.enums.ShopOrderStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * @author 张治保
 * date 2022/6/23
 */
@Getter
@RequiredArgsConstructor
public enum OrderQueryStatus {

    /**
     * 待付款
     */
    UNPAID(
            Set.of(OrderStatus.UNPAID),
            Set.of(),
            Set.of()
    ),
    /**
     * 待发货
     */
    UN_DELIVERY(
            Set.of(OrderStatus.PAID),
            Set.of(),
            Set.of(PackageStatus.WAITING_FOR_DELIVER)
    ),
    /**
     * 待收货
     */
    UN_RECEIVE(
            Set.of(OrderStatus.PAID),
            Set.of(),
            Set.of(PackageStatus.WAITING_FOR_RECEIVE)
    ),
    /**
     * 待评价
     */
    UN_COMMENT(
            Set.of(OrderStatus.PAID),
            Set.of(),
            Set.of(PackageStatus.BUYER_WAITING_FOR_COMMENT, PackageStatus.SYSTEM_WAITING_FOR_COMMENT)
    ),
    /**
     * 已完成
     */
    COMPLETED(
            Set.of(OrderStatus.PAID),
            Set.of(),
            Set.of(PackageStatus.BUYER_COMMENTED_COMPLETED, PackageStatus.SYSTEM_COMMENTED_COMPLETED)
    ),
    /**
     * 已关闭
     */
    CLOSED(
            Set.of(OrderStatus.BUYER_CLOSED, OrderStatus.SELLER_CLOSED, OrderStatus.SYSTEM_CLOSED, OrderStatus.TEAM_FAIL),
            Set.of(ShopOrderStatus.BUYER_CLOSED, ShopOrderStatus.SELLER_CLOSED, ShopOrderStatus.SYSTEM_CLOSED),
            Set.of()
    );

    //对应的订单状态列表
    private final Set<OrderStatus> orderStatus;
    //对应的店铺订单状态列表 仅需枚举已关闭的状态 其它状态皆使用正常状态
    private final Set<ShopOrderStatus> closeShopOrderStatus;
    //对应的店铺订单商品包裹状态
    private final Set<PackageStatus> packageStatus;


}

package com.medusa.gruul.addon.supplier.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * @author 张治保
 * date 2023/7/24
 */
@Getter
@RequiredArgsConstructor
public enum OrderQueryStatus {
    /**
     * 待支付
     */
    UNPAID(Set.of(OrderStatus.UNPAID), Set.of()),

    /**
     * 待审核
     */
    PAYMENT_AUDIT(Set.of(OrderStatus.PAYMENT_AUDIT), Set.of()),

    /**
     * 待发货
     */
    WAITING_FOR_DELIVER(Set.of(OrderStatus.PAID), Set.of(PackageStatus.WAITING_FOR_DELIVER)),

    /**
     * 待入库
     */
    WAITING_FOR_PUTIN(Set.of(OrderStatus.PAID), Set.of(PackageStatus.WAITING_FOR_RECEIVE)),

    /**
     * 已完成
     */
    FINISHED(Set.of(OrderStatus.PAID), Set.of(PackageStatus.COMPLETED)),

    /**
     * 已关闭
     */
    CLOSED(Set.of(OrderStatus.AUDIT_FAIL_CLOSED, OrderStatus.SYSTEM_CLOSED, OrderStatus.SELLER_CLOSED, OrderStatus.BUYER_CLOSED), Set.of()),

    ;

    private final Set<OrderStatus> orderStatuses;

    private final Set<PackageStatus> packageStatuses;
}

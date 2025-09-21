package com.medusa.gruul.order.api.enums;

/**
 * 订单关闭类型
 *
 * @author 张治保
 * date 2022/11/11
 */
public enum OrderCloseType {
    /**
     * 关闭整个订单
     */
    FULL,

    /**
     * 关闭店铺订单
     */
    SHOP,

    /**
     * 售后关闭
     */
    AFS
}

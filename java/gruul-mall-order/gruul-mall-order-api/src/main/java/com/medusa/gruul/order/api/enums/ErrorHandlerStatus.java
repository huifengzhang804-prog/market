package com.medusa.gruul.order.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2024/8/29
 */
@Getter
@RequiredArgsConstructor
public enum ErrorHandlerStatus {
    /**
     * 重新发货
     */
    RESHIP,

    /**
     * 已送达（待收货）
     */
    DELIVERED,

    /**
     * 关闭且退款
     */
    CLOSE_REFUND


}

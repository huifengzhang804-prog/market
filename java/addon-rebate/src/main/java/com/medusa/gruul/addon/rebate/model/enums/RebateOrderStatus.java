package com.medusa.gruul.addon.rebate.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum RebateOrderStatus {
    /**
     * 待付款
     */
    UNPAID(1),

    /**
     * 已付款
     */
    PAID(2),

    /**
     * 已完成
     */
    COMPLETED(3),

    /**
     * 已关闭
     */
    CLOSED(4);

    @EnumValue
    private final Integer value;


    RebateOrderStatus(Integer value) {
        this.value = value;
    }
}

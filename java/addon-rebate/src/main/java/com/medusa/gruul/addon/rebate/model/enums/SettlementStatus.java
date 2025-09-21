package com.medusa.gruul.addon.rebate.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;


/**
 * 消费返利结算状态
 */

@Getter
public enum SettlementStatus {

    /**
     * 待结算
     */

    PENDING_SETTLEMENT(0),

    /**
     * 已结算
     */
    SETTLED(1),

    /**
     * 已失效
     */
    EXPIRED(2),
    ;


    @EnumValue
    private final Integer value;

    SettlementStatus(Integer value) {
        this.value = value;
    }
}

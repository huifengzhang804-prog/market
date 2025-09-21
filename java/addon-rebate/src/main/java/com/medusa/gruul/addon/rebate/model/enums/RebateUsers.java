package com.medusa.gruul.addon.rebate.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum RebateUsers {



    /**
     * 付费会员
     */
    PAID_MEMBER(0),

    /**
     * 所有会员
     */
    ALL_MEMBERS(1),;


    @EnumValue
    private final Integer value;

    RebateUsers(Integer value) {
        this.value = value;
    }
}

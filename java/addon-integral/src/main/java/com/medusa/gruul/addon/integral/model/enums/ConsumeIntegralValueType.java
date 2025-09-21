package com.medusa.gruul.addon.integral.model.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 *  消费积分类型
 */
@Getter
public enum ConsumeIntegralValueType {


    /**
     * 订单数量
     */
    ORDER_QUANTITY(1,"订单数量"),

    /**
     * 订单金额
     */
    ORDER_AMOUNT(2,"订单金额");


    @EnumValue
    private final Integer code;

    private final String desc;

  ConsumeIntegralValueType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

package com.medusa.gruul.user.api.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author wudi
 *  消费成长值类型
 */
@Getter
public enum ConsumeGrowthValueType {


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

  ConsumeGrowthValueType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

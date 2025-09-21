package com.medusa.gruul.common.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 销售类型
 *
 * @author xiaoq
 * @Description SellType.java
 * @date 2023-07-17 10:10
 */
@Getter
@RequiredArgsConstructor
public enum SellType {
    /**il
     * 采购
     */
    PURCHASE(0, "采购"),

    /**
     * 代销l
     */
    CONSIGNMENT(1,"代销"),

    /**
     * 自有商品
     */
    OWN(2,"自有商品"),
    ;
    @EnumValue
    private final Integer value;

    private final String desc;
}

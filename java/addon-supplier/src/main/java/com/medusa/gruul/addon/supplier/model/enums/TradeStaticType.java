package com.medusa.gruul.addon.supplier.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p>商品交易统计枚举</p>
 * @author An.Yan
 */
@Getter
@RequiredArgsConstructor
public enum TradeStaticType {

    /**
     * 商品交易数量
     */
    TRADE_NUMBER(1),

    /**
     * 商品交易金额
     */
    TRADE_AMOUNT(2),
    ;


    @EnumValue
    private final Integer value;
}

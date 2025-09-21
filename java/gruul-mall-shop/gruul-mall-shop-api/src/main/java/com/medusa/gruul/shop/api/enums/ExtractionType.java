package com.medusa.gruul.shop.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 抽佣类型
 *
 * @author xiaoq
 * @Description ExtractionType.java
 * @date 2023-05-18 19:06
 */
@Getter
@RequiredArgsConstructor
public enum ExtractionType {
    /**
     * 类目抽佣
     */
    CATEGORY_EXTRACTION(0),


    /**
     * 订单销售额抽佣
     */
    ORDER_SALES_EXTRACTION(1)
    ;

    @EnumValue
    private final Integer value;
}

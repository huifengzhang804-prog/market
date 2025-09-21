package com.medusa.gruul.storage.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 
 *     限购类型
 * 
 *
 * @author 张治保
 * date 2022/6/21
 */
@Getter
@RequiredArgsConstructor
public enum LimitType {
    /**
     * 不限购(无限个)
     */
    UNLIMITED(1),
    /**
     * 商品限购
     */
    PRODUCT_LIMITED(2),
    /**
     * 规格限购
     */
    SKU_LIMITED(3);
    @EnumValue
    private final Integer value;
}

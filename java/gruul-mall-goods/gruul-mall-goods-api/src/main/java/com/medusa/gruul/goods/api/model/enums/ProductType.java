package com.medusa.gruul.goods.api.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 商品类型
 *
 * @author xiaoq
 * @Description ProductType.java
 * @date 2023-06-17 11:06
 */
@Getter
@RequiredArgsConstructor
public enum ProductType {
    /**
     * 真实商品
     */
    REAL_PRODUCT(0, "实物商品"),

    /**
     * 虚拟商品
     */
    VIRTUAL_PRODUCT(1, "虚拟商品"),
    ;


    @EnumValue
    private final Integer value;

    private final String desc;
}

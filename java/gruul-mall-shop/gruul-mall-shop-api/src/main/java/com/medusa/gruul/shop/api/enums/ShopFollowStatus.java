package com.medusa.gruul.shop.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 店铺关注枚举
 * @author: WuDi
 * @date: 2022/9/2
 */
@Getter
@RequiredArgsConstructor
public enum ShopFollowStatus {

    /**
     * 全部店铺
     */
    ALL_SHOP(0),

    /**
     * 最近的
     */
    RECENTLY(1),
    /**
     * 上新商品
     */
    NEW_PRODUCTS(2);

    @EnumValue
    private final Integer value;

}

package com.medusa.gruul.goods.api.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 我的关注枚举
 * @author WuDi
 */
@Getter
@RequiredArgsConstructor
public enum MyShopFollowStatus {

    /**
     * 全部店铺
     */
    ALL_SHOP(0),

    /**
     * 最近查看
     */
    RECENTLY(1),

    /**
     * 上新商品
     */
    NEW_PRODUCTS(2);

    @EnumValue
    private final Integer value;
}

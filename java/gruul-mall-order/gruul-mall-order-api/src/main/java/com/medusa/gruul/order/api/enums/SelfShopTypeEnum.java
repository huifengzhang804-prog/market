package com.medusa.gruul.order.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author: wufuzhong
 * @Date: 2023/11/07 18:30:50
 * @Description: 店铺发货类型
 */

@Getter
@RequiredArgsConstructor
public enum SelfShopTypeEnum {

    /**
     * 自营店铺
     */
    SELF_SHOP(0),

    /**
     * 自营供应商
     */
    SELF_SUPPLIER(1);

    @EnumValue
    private final Integer value;
}

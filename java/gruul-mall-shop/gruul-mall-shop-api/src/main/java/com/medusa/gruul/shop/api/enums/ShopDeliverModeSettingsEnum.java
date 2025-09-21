package com.medusa.gruul.shop.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author: wufuzhong
 * @Date: 2023/11/07 09:58:58
 * @Description: 自营店铺发货方式enum
 */
@Getter
@RequiredArgsConstructor
public enum ShopDeliverModeSettingsEnum {

    /**
     * 店铺、供应商自己发货
     */
    OWN(0),

    /**
     * 平台发货
     */
    PLATFORM(1);

    @EnumValue
    private final Integer value;
}

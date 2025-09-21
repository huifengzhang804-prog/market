package com.medusa.gruul.addon.store.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author xiaoq
 * @Description 门店状态枚举
 * @date 2023-03-08 14:51
 */
@Getter
@RequiredArgsConstructor
public enum StoreStatus {

    /**
     * 正常
     */
    NORMAL(0),
    /**
     * 店铺禁用
     */
    SHOP_FORBIDDEN(1),

    /**
     * 平台禁用
     */
    PLATFORM_FORBIDDEN(2),
    ;

    @EnumValue
    private final Integer value;
}

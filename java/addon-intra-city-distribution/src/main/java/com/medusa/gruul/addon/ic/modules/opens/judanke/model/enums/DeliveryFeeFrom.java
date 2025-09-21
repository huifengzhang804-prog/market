package com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2024/8/7
 */
@RequiredArgsConstructor
@Getter
public enum DeliveryFeeFrom {
    /**
     * 开
     */
    PLATFORM(0),
    /**
     * 关
     */
    MERCHANT(1);
    @JSONField
    private final int value;
}

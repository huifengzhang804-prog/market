package com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 运力类型
 *
 * @author 张治保
 * @since 2024/8/7
 */
@RequiredArgsConstructor
@Getter
public enum DeliveryType {
    /**
     * 绑定运力
     */
    BIND(1),
    /**
     * 聚合运力
     */
    POLYMERIZE(0);
    @JSONField
    private final int value;
}

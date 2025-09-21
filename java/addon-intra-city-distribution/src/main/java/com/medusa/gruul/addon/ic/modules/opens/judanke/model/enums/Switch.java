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
public enum Switch {
    /**
     * 开
     */
    ON(1),
    /**
     * 关
     */
    OFF(0);
    @JSONField
    private final int value;
}

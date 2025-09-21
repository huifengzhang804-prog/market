package com.medusa.gruul.addon.platform.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p>模板类型枚举</p>
 * @author An.Yan
 */
@Getter
@RequiredArgsConstructor
public enum TemplateTypeEnum {

    /**
     * 平台
     */
    PLATFORM(1),
    /**
     * 店铺
     */
    SHOP(2);
    @EnumValue
    private final Integer value;
}

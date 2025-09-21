package com.medusa.gruul.shop.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *  模板业务类型枚举  
 * @author An.Yan
 */
@Getter
@RequiredArgsConstructor
public enum TemplateBusinessTypeEnum {
    /**
     * 线上业务
     */
    ONLINE(1),
    /**
     * O2O业务
     */
    O2O(2);
    @EnumValue
    private final Integer value;
}

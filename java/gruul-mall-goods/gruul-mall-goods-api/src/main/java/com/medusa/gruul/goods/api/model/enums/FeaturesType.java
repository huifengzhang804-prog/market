package com.medusa.gruul.goods.api.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 产品特性类型
 *
 * @author xiaoq
 * @Description FeaturesType.java
 * @date 2023-06-14 20:38
 */
@Getter
@RequiredArgsConstructor
public enum FeaturesType {
    /**
     * 属性
     */
    ATTRIBUTE(0),


    /**
     * 参数
     */
    ARGUMENTS(1),
    ;

    @EnumValue
    private final Integer value;
}

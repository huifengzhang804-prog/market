package com.medusa.gruul.goods.api.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 违规类型
 *
 * @author xiaoq
 * @Description ViolationType.java
 * @date 2023-06-15 15:34
 */
@Getter
@RequiredArgsConstructor
public enum ViolationType {

    /**
     * 违禁品
     */
    PROHIBITED(0),


    /**
     * 仿冒
     */
    COUNTERFEIT(1),

    /**
     * 平台接入率过高
     */
    EXCESSIVE_PLATFORM_INTERVENTION(2),


    /**
     * 标题不合规
     */
    TITLE_IRREGULARITY(3),

    /**
     * 其他
     */
    OTHER(4),
    ;

    @EnumValue
    private final Integer value;

}

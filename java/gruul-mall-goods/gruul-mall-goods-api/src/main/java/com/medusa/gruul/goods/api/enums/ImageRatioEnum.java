package com.medusa.gruul.goods.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 图片比例枚举
 *
 * @author jipeng
 * @since 2025/1/8
 */
@Getter
@RequiredArgsConstructor
public enum ImageRatioEnum {
    /**
     * 1比1
     */
    RATIO1_1(1),
    /**
     * 1比2
     */
    RATIO1_2(2);

    @EnumValue
    private final Integer code;
}

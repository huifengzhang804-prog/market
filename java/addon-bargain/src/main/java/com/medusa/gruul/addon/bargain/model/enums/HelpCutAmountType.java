package com.medusa.gruul.addon.bargain.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 砍价类型
 * @author wudi
 */
@Getter
@RequiredArgsConstructor
public enum HelpCutAmountType {

    /**
     * 随机砍价
     */
    RANDOM_BARGAIN(0),

    /**
     * 固定砍价
     */
    FIX_BARGAIN(1);

    @EnumValue
    private final Integer value;
}

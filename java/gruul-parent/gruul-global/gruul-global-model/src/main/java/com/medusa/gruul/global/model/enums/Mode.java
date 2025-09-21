package com.medusa.gruul.global.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 运行业务模式
 * @author 张治保
 * date 2022/4/18
 */
@Getter
@RequiredArgsConstructor
public enum  Mode {
    /**
     * B2C
     */
    B2C(0),
    /**
     * B2B2C
     */
    B2B2C(1),
    /**
     * B2B
     */
    B2B(2),
    /**
     * S2B2C
     */
    S2B2C(3),

    /**
     * O2O
     */
    O2O(4);


    private final Integer value;
}

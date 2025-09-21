package com.medusa.gruul.addon.bargain.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 砍价状态
 * @author qskj
 */
@Getter
@RequiredArgsConstructor
public enum BargainStatus {

    /**
     * 砍价中
     */
    BARGAINING(0),

    /**
     * 砍价失败
     */
    FAILED_TO_BARGAIN(1),

    /**
     * 砍价成功
     */
    SUCCESSFUL_BARGAIN(2);

    @EnumValue
    private final Integer value;

}

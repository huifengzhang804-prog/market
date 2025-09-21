package com.medusa.gruul.addon.platform.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2022/4/25
 */
@Getter
@RequiredArgsConstructor
public enum  ClientConfigType {
    /**
     * 平台
     */
    PLATFORM(0),
    /**
     * 商家
     */
    SHOP(1),
    /**
     * 小程序
     */
    MINI_PROGRAM(2),
    /**
     * APP
     */
    APP(3),
    /**
     * H5
     */
    H5(4);
    @EnumValue
    private final Integer value;
}

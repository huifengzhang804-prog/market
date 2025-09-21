package com.medusa.gruul.common.system.model.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * APP平台类型
 * @author 张治保
 * date 2021/11/30
 */
@Getter
@RequiredArgsConstructor
public enum Platform {
    /**
     * 微信小程序
     */
    WECHAT_MINI_APP(0),
    /**
     * 公众号
     */
    WECHAT_MP(1),
    /**
     * PC端
     */
    PC(2),
    /**
     * 移动端端h5
     */
    H5(3),
    /**
     * IOS
     */
    IOS(4),
    /**
     * ANDROID
     */
    ANDROID(5),
    /**
     * HARMONY
     */
    HARMONY(6);


    @EnumValue
    private final Integer value;
}

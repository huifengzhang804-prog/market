package com.medusa.gruul.common.custom.aggregation.decoration.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 聚合 APP平台类型
 *
 * @author xiaoq
 * @Description AggregationPlatform.java
 * @date 2022-11-02 17:04
 */
@Getter
@RequiredArgsConstructor
public enum AggregationPlatform {

    /**
     * 微信小程序
     */
    WECHAT_MINI_APP(0),

    /**
     * PC端
     */
    PC(1),

    /**
     * 其他 公众号+移动端端h5+IOS+ANDROID+HARMONY
     */
    OTHERS(2);

    @EnumValue
    private final Integer value;

}

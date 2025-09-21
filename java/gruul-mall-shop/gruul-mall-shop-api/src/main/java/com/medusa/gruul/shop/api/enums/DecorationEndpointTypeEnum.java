package com.medusa.gruul.shop.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *  装修终端类型枚举  
 * @author An.Yan
 */
@Getter
@RequiredArgsConstructor
public enum DecorationEndpointTypeEnum {
    /**
     * 微信小程序
     */
    WECHAT_MINI_APP(1),
    /**
     * H5,APP端
     */
    H5_APP(2),
    /**
     * PC商城
     */
    PC_MALL(3);
    @EnumValue
    private final Integer value;
}

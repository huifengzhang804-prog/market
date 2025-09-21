package com.medusa.gruul.addon.live.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author miskw
 * @date 2023/6/12
 * @describe 禁播类型
 */
@Getter
public enum ProhibitedType {
    /**
     * 直播间
     */
    LIVE(0),
    /**
     * 主播
     */
    ANCHOR(1)
    ;
    @EnumValue
    private final int value;

    ProhibitedType(int value) {
        this.value = value;
    }
}

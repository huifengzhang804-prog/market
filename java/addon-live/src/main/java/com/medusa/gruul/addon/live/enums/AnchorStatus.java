package com.medusa.gruul.addon.live.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author miskw
 * @date 2023/6/5
 * @describe 主播状态
 */
@Getter
public enum AnchorStatus {
    /**
     * 正常
     */
    NORMAL(0),
    /**
     * 禁用
     */
    FORBIDDEN(1),
    /**
     * 违规禁用
     */
    VIOLATION(2);
    @EnumValue
    private final int value;

    AnchorStatus(int value) {
        this.value = value;
    }
}

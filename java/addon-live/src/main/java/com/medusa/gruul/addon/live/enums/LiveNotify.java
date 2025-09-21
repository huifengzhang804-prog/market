package com.medusa.gruul.addon.live.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author miskw
 * @date 2023/6/6
 * @describe 描述
 */
@Getter
public enum LiveNotify {
    /**
     * 开播
     */
    RECORD("record"),
    /**
     * 断播
     */
    SNAPSHOT("snapshot");
    @EnumValue
    private final String desc;

    LiveNotify(String desc) {
        this.desc = desc;
    }

    public static LiveNotify getLiveNotify(String desc) {
        for (LiveNotify value : LiveNotify.values()) {
            if (value.getDesc().equals(desc)) {
                return value;
            }
        }
        return null;
    }
}

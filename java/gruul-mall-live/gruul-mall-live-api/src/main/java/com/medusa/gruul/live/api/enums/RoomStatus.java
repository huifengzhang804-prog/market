package com.medusa.gruul.live.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author miskw
 * @date 2022/11/8
 * @describe 直播间状态
 */
@Getter
public enum RoomStatus {
    /**
     * 未开始
     */
    NOT_STARTED(0, "未开始"),
    /**
     * 直播中
     */
    LIVE_BROADCAST(1, "直播中"),
    /**
     * 已结束
     */
    CLOSED(2, "已结束");
    @EnumValue
    private int code;
    private String describe;

    RoomStatus(int code, String describe) {
        this.code = code;
        this.describe = describe;
    }

    public int getCode() {
        return code;
    }

    public String getDescribe() {
        return describe;
    }

}

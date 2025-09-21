package com.medusa.gruul.addon.live.socket.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author miskw
 * @date 2023/6/19
 * @describe socket消息类型
 */
@Getter
public enum SocketMessageType {

    /**
     * 点赞
     */
    LIKE(1),
    /**
     * 观看
     */
    VIEWERSHIP(2);
    @EnumValue
    private final int value;

    SocketMessageType(int value) {
        this.value = value;
    }

    public SocketMessageType getSocketMessageType(Integer value) {
        for (SocketMessageType socketMessageType : values()) {
            if (socketMessageType.getValue() == value) {
                return socketMessageType;
            }
        }
        return null;
    }
}

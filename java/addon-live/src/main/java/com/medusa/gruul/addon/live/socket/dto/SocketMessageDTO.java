package com.medusa.gruul.addon.live.socket.dto;

import com.medusa.gruul.addon.live.socket.enums.SocketMessageType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author miskw
 * @date 2023/6/19
 * @describe socket消息数据传输对象
 */
@Getter
@Setter
@Accessors(chain = true)
public class SocketMessageDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 消息类型
     */
    private SocketMessageType type;
    /**
     * 消息数据
     */
    private Object data;
}

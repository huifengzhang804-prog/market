package com.medusa.gruul.addon.live.socket.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author miskw
 * @date 2023/6/19
 * @describe 描述
 */
@Getter
@Setter
@Accessors(chain = true)
public class InteractionSocketDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 42L;
    /**
     * 直播间id
     */
    private String liveId;
    /**
     * 数量
     */
    private Integer count;
}

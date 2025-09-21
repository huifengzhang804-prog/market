package com.medusa.gruul.addon.live.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author miskw
 * @date 2023/6/20
 * @describe 描述
 */
@Getter
@Setter
@Accessors(chain = true)
public class LiveRoomMessageVO {
    /**
     * 直播间id
     */
    private Long id;
    /**
     * 推流地址
     */
    private String pushAddress;

}

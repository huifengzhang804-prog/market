package com.medusa.gruul.carrier.pigeon.service.model;

import com.medusa.gruul.carrier.pigeon.service.mp.entity.Message;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 张治保
 * date 2022/10/10
 */
@Getter
@Setter
public class Notice implements Serializable {

    /**
     * 是否展示红点提示
     */
    private Boolean show;

    /**
     * 消息的最后发送时间
     */
    private LocalDateTime lastTime;

    /**
     * 最后一条消息
     */
    private Message lastMessage;

}

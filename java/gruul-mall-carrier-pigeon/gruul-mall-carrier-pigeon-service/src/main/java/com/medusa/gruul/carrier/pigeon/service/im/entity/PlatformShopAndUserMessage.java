package com.medusa.gruul.carrier.pigeon.service.im.entity;

import com.medusa.gruul.carrier.pigeon.api.enums.Channel;
import com.medusa.gruul.carrier.pigeon.api.enums.MessageType;
import com.medusa.gruul.carrier.pigeon.api.model.BaseMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * 供应商-店铺消息封装类
 * @author An.Yan
 */
@Getter
@Setter
@Accessors(chain = true)
public class PlatformShopAndUserMessage implements BaseMessage {

    /**
     * 消息频道
     */
    private final Channel channel = Channel.PLATFORM_SHOP_AND_USER;

    /**
     * 消息发送方
     */
    private ChatMessageSender sender;

    /**
     * 消息接收方
     */
    private ChatMessageReceiver receiver;

    /**
     * 消息媒体类型
     */
    private MessageType messageType;

    /**
     * 内容
     */
    private String message;

    private Long senderTime;

    private Boolean read;
}

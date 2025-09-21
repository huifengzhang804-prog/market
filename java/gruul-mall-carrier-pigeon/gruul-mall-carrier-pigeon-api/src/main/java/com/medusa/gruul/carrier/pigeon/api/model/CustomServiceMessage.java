package com.medusa.gruul.carrier.pigeon.api.model;

import com.medusa.gruul.carrier.pigeon.api.enums.Channel;
import com.medusa.gruul.carrier.pigeon.api.enums.MessageType;
import com.medusa.gruul.carrier.pigeon.api.enums.UserType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * 客服消息枚举
 *
 * @author 张治保
 * date 2022/10/12
 */
@Getter
@Setter
@Accessors(chain = true)
public class CustomServiceMessage implements BaseMessage {

    /**
     * 消息频道
     */
    private final Channel channel = Channel.CUSTOMER_SERVICE;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 消息id
     */
    private Long messageId;

    /**
     * 发送方类型
     */
    private UserType senderType;

    /**
     * 消息发送方id
     */
    private Long senderId;

    /**
     * 接收方类型
     */
    private UserType receiverType;

    /**
     * 接收方id
     */
    private Long receiverId;

    /**
     * 消息内容类型
     */
    private MessageType messageType;

    /**
     * 消息内容
     */
    private String message;


}

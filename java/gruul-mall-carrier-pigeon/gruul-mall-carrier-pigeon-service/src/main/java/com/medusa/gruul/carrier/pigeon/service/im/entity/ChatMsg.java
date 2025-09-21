package com.medusa.gruul.carrier.pigeon.service.im.entity;

import com.medusa.gruul.carrier.pigeon.api.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


/**
 * 供应商-店铺消息封装
 *
 * @author An.Yan
 */
@Getter
@Setter
public class ChatMsg {

    /**
     * 消息类型
     */
    @NotNull
    private MessageType messageType;

    /**
     * 消息内容
     */
    @NotBlank
    private String content;

    /**
     * 接收方店铺ID
     */
    @NotNull
    private Long receiverShopId;

    /**
     * 接收方用户ID
     */
    //@NotNull
    private Long receiverUserId;

    /**
     * 发送方店铺ID
     */
    private Long senderShopId;

    /**
     * 发送方用户ID
     */
    private Long senderUserId;

}

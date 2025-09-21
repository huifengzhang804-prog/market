package com.medusa.gruul.carrier.pigeon.service.im.entity;

import com.medusa.gruul.carrier.pigeon.api.enums.UserType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>消息发送方实体类</p>
 * @author An.Yan
 */
@Getter
@Setter
@Accessors(chain = true)
public class ChatMessageSender {

    /**
     * 发送方店铺ID
     */
    private ShopInfo senderShopInfo;

    /**
     * 发送方用户ID
     */
    private UserInfo senderUserInfo;

    /**
     * 消息发送者类型
     */
    private UserType senderType;

}

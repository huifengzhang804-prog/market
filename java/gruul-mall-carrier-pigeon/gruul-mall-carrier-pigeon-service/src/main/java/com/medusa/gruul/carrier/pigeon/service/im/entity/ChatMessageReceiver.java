package com.medusa.gruul.carrier.pigeon.service.im.entity;

import com.medusa.gruul.carrier.pigeon.api.enums.UserType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>消息接收方实体类</p>
 * @author An.Yan
 */
@Getter
@Setter
@Accessors(chain = true)
public class ChatMessageReceiver {

    /**
     * 接收方店铺ID
     */
    private ShopInfo receiverShopInfo;

    /**
     * 接收方用户ID
     */
    private UserInfo receiverUserInfo;

    /**
     * 消息接受者类型
     */
    private UserType receiverType;
}

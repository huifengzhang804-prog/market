package com.medusa.gruul.carrier.pigeon.service.im.model.vo;

import com.medusa.gruul.carrier.pigeon.service.im.entity.ChatMessage;
import com.medusa.gruul.carrier.pigeon.service.im.entity.ShopInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author An.Yan
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ChatRoomVO {

    private ShopInfo chatWithShopInfo;

    /**
     * 最后一条消息
     */
    private ChatMessage lastMessage;
}

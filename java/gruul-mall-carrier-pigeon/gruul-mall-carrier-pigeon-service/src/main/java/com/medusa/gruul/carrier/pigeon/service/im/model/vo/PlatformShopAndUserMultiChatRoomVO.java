package com.medusa.gruul.carrier.pigeon.service.im.model.vo;

import com.medusa.gruul.carrier.pigeon.service.im.entity.ChatMessage;
import com.medusa.gruul.carrier.pigeon.service.im.entity.ShopInfo;
import com.medusa.gruul.carrier.pigeon.service.im.entity.UserInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author yanan
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PlatformShopAndUserMultiChatRoomVO {

    private UserInfo chatWithUserInfo;

    private ChatMessage lastMessage;

    private ShopInfo chatWithShopInfo;
}

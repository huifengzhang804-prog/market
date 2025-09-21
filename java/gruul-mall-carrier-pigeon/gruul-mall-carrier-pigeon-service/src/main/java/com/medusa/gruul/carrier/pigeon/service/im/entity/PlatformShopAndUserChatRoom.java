package com.medusa.gruul.carrier.pigeon.service.im.entity;

import com.medusa.gruul.carrier.pigeon.service.im.constants.IMRedisConstant;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>平台/店铺-用户聊天室</p>
 * @author An.Yan
 */
@Getter
@Setter
public class PlatformShopAndUserChatRoom {

    private Long shopId;

    private Long userId;

    private final String roomKey;

    private final String roomValue;

    private final String roomId;

    private Long lastUpdateTime;


    public PlatformShopAndUserChatRoom(Long shopId, Long userId, Long lastUpdateTime) {
        this.shopId = shopId;
        this.userId = userId;
        this.lastUpdateTime = lastUpdateTime;
        this.roomKey = String.format(IMRedisConstant.IM_PLATFORM_CHATROOM_KEY, shopId);
        if (shopId >= userId) {
            this.roomValue = userId + ":" + shopId;
            this.roomId = String.format(IMRedisConstant.IM_PLATFORM_ROOM_KEY, userId, shopId);
        } else {
            this.roomValue = shopId + ":" + userId;
            this.roomId = String.format(IMRedisConstant.IM_PLATFORM_ROOM_KEY, shopId, userId);
        }

    }

    public PlatformShopAndUserChatRoom(Long shopId) {
        this.shopId = shopId;
        this.roomKey = String.format(IMRedisConstant.IM_PLATFORM_CHATROOM_KEY, shopId);
        this.roomValue = null;
        this.roomId = null;
    }

    public String getRoomKey() {
        return this.roomKey;
    }

    public String getRoomValue() {
        return this.roomValue;
    }

    public Long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public String getRoomId() {
        return this.roomId;
    }

    public PlatformShopAndUserChatRoom reverse() {
        return new PlatformShopAndUserChatRoom(this.userId, this.shopId, this.lastUpdateTime);
    }
}

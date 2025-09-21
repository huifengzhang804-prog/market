package com.medusa.gruul.carrier.pigeon.service.im.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.medusa.gruul.carrier.pigeon.service.im.constants.IMRedisConstant;

import java.time.LocalDateTime;

import static com.medusa.gruul.carrier.pigeon.service.im.constants.DateConstant.DATE_FORMAT;

/**
 * <p>群聊聊天室</p>
 * @author An.Yan
 */
public class GroupChatRoom {
    @JSONField()
    private Long fromShopId;
    @JSONField()
    private Long toShopId;

    /**
     * 群聊聊天室Key,指向店铺所拥有的rooms(ZSet)
     */
    @JSONField()
    private final String groupChatRoomKey;

    /**
     * 群聊聊天室Value,存储:fromShopId-toShopId
     */
    @JSONField
    private final String groupChatRoomValue;

    /**
     * 聊天室ID,根据{@link IMRedisConstant#IM_ROOM_KEY}
     * 指向一个具体的聊天室
     */
    @JSONField()
    private final String groupChatRoomId;

    @JSONField(serialize = false)
    private Long lastUpdateTime;

    @JSONField(format = DATE_FORMAT)
    private final LocalDateTime createTime;

    /**
     * 创建聊天室对象,按照{@code fromShopId}和{@code toShopId}的大小,生成
     * {@link GroupChatRoom#groupChatRoomId}和{@link GroupChatRoom#groupChatRoomValue}
     * @param fromShopId 发送方店铺ID
     * @param toShopId 接收方店铺ID
     * @param lastUpdateTime 最后更新时间
     */
    public GroupChatRoom(Long fromShopId, Long toShopId, LocalDateTime createTime, Long lastUpdateTime) {
        this.fromShopId = fromShopId;
        this.toShopId = toShopId;
        this.groupChatRoomKey = String.format(IMRedisConstant.IM_GROUP_CHAT_ROOM_KEY, fromShopId);
        this.createTime = createTime;
        if (fromShopId >= toShopId) {
            this.groupChatRoomId = String.format(IMRedisConstant.IM_ROOM_KEY, toShopId, fromShopId);
            this.groupChatRoomValue = toShopId + ":" + fromShopId;
        } else {
            this.groupChatRoomId = String.format(IMRedisConstant.IM_ROOM_KEY, fromShopId, toShopId);
            this.groupChatRoomValue = fromShopId + ":" + toShopId;
        }
        this.lastUpdateTime = lastUpdateTime;
    }

    /**
     * 创建聊天室对象,用于{@code currShopId}指向的聊天室Key
     * @param currShopId
     */
    public GroupChatRoom(Long currShopId) {
        this.groupChatRoomKey = String.format(IMRedisConstant.IM_GROUP_CHAT_ROOM_KEY, currShopId);
        this.groupChatRoomValue = null;
        this.groupChatRoomId = null;
        this.createTime = null;
    }

    public String getGroupChatRoomKey() {
        return this.groupChatRoomKey;
    }

    public String getGroupChatRoomValue() {
        return this.groupChatRoomValue;
    }

    public Long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public String getGroupChatRoomId() {
        return this.groupChatRoomId;
    }

    public Long getFromShopId() {
        return this.fromShopId;
    }

    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public Long getToShopId() {
        return this.toShopId;
    }


    public void setLastUpdateTime(Long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

}

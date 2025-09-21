package com.medusa.gruul.carrier.pigeon.service.service;

import com.medusa.gruul.carrier.pigeon.service.model.dto.ChatMessage;

/**
 * @author 张治保
 * date 2022/10/12
 */
public interface ManageMessageService {

    /**
     * 发送消息
     *
     * @param userId      用户id
     * @param chatMessage 聊天消息
     */
    void sendMessage(Long userId, ChatMessage chatMessage);

    /**
     * 商家端已读用户发送的消息
     * @param shopId shopId
     * @param adminId 店铺管理者Id
     */
    void readUserMessages( Long shopId,Long adminId);

    /**
     * 移动端删除消息列表消息
     * @param shopId 店铺id
     * @param adminId 店铺管理员id
     * @param userId 用户id
     */
    void deleteMessage(Long shopId,Long adminId, Long userId);

    /**
     * 移动端商家后台未读消息总数
     * @param shopId 店铺id
     * @param adminId 管理员id
     * @return 店铺未读消息数量
     */
    Long getUserToShopUnreadCount(Long shopId,Long adminId);

    /**
     * 商家根据用户id已读用户消息
     *
     * @param userId 用户id
     * @param shopId 店铺id
     * @param adminId 店铺管理员id
     */
    void readUserMessagesByUserId(Long userId, Long shopId, Long adminId);

    /**
     *  平台发送消息至用户
     *
     * @param userId 用户id
     * @param chatMessage 消息体
     */
    void sendMessageToUser(Long userId, ChatMessage chatMessage);

}

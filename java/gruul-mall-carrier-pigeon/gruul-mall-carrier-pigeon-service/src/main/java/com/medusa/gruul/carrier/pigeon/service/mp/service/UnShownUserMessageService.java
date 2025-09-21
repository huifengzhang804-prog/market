package com.medusa.gruul.carrier.pigeon.service.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.carrier.pigeon.api.enums.UserType;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.UnShownUserMessage;

/**
 * @author miskw
 * @date 2023/3/1
 */
public interface UnShownUserMessageService extends IService<UnShownUserMessage> {
    /**
     * 移动端删除消息列表消息
     * @param shopId 店铺id
     * @param adminId 店铺管理员id
     * @param userId 用户id
     * @param senderType 消息发送者
     * @param receiverType 消息接收方
     */
    void deleteMessage(Long shopId,Long adminId ,Long userId,UserType senderType,UserType receiverType);

    /**
     * 消息列表展示
     * @param userId 用户id
     * @param shopId 店铺id
     * @param consumer 消息发送者类型
     * @param shopAdmin 消息接收者类型
     */
    void showDeleteMessage(Long userId, Long shopId, UserType consumer, UserType shopAdmin);
}

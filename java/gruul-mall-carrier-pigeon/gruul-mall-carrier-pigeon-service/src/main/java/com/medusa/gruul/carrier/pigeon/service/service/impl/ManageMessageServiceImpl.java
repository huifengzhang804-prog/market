package com.medusa.gruul.carrier.pigeon.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.carrier.pigeon.api.enums.SendType;
import com.medusa.gruul.carrier.pigeon.api.enums.UserType;
import com.medusa.gruul.carrier.pigeon.api.model.CustomServiceMessage;
import com.medusa.gruul.carrier.pigeon.service.model.PigeonConst;
import com.medusa.gruul.carrier.pigeon.service.model.dto.ChatMessage;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.Message;
import com.medusa.gruul.carrier.pigeon.service.mp.service.IMessageService;
import com.medusa.gruul.carrier.pigeon.service.mp.service.UnShownUserMessageService;
import com.medusa.gruul.carrier.pigeon.service.service.ManageMessageService;
import com.medusa.gruul.carrier.pigeon.service.service.MessageSender;
import com.medusa.gruul.carrier.pigeon.service.service.ShopAndUserService;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.global.model.helper.CompletableTask;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Executor;

/**
 * @author 张治保
 * date 2022/10/12
 */
@Service
@RequiredArgsConstructor
public class ManageMessageServiceImpl implements ManageMessageService {

    private final IMessageService messageService;
    private final ShopAndUserService shopAndUserService;
    private final Executor globalExecutor;
    private final MessageSender messageSender;
    private final UnShownUserMessageService shownUserMessageService;


    @Override
    @Redisson(value = PigeonConst.PIGEON_SEND_USER_MSG_LOCK, key = "#userId")
    @Transactional(rollbackFor = Exception.class)
    public void sendMessage(Long userId, ChatMessage chatMessage) {
        SecureUser user = ISecurity.userMust();
        Long shopId = user.getShopId();
        Long adminId = user.getId();
        //检查店铺资料
        shopAndUserService.checkAndGetMessageShop(shopId);
        menageSendMessageCommonLogic(shopId, adminId, userId, chatMessage, UserType.SHOP_ADMIN);
    }

    /**
     * 商家端已读用户发送的消息
     *
     * @param shopId  shopId
     * @param adminId 店铺管理者Id
     */
    @Override
    @Redisson(value = PigeonConst.PIGEON_SEND_USER_MSG_LOCK, key = "#adminId+':'+#shopId")
    public void readUserMessages(Long shopId, Long adminId) {
        ISystem.shopId(shopId,
                () -> messageService.lambdaUpdate()
                        .set(Message::getRead, Boolean.TRUE)
                        .set(Message::getAdminId, adminId)
                        .eq(Message::getSenderType, UserType.CONSUMER)
                        .eq(Message::getReceiverType, UserType.SHOP_ADMIN)
                        .eq(Message::getRead, Boolean.FALSE)
                        .and(qw -> qw.eq(Message::getAdminId, adminId).or().isNull(Message::getAdminId))
                        .update()
        );

    }

    /**
     * 移动端删除消息列表消息
     *
     * @param shopId  店铺id
     * @param adminId 店铺管理员id
     * @param userId  用户id
     */
    @Override
    public void deleteMessage(Long shopId, Long adminId, Long userId) {
        shownUserMessageService.deleteMessage(shopId, adminId, userId, UserType.CONSUMER, UserType.SHOP_ADMIN);
    }

    /**
     * 移动端商家后台未读消息总数
     *
     * @param shopId 店铺id
     * @return 店铺未读消息数量
     */
    @Override
    public Long getUserToShopUnreadCount(Long shopId, Long adminId) {
        return ISystem.shopId(shopId, () -> messageService.lambdaQuery()
                .eq(Message::getRead, Boolean.FALSE)
                .eq(Message::getSenderType, UserType.CONSUMER)
                .eq(Message::getReceiverType, UserType.SHOP_ADMIN)
                .and(qw -> qw.eq(Message::getAdminId, adminId).or().isNull(Message::getAdminId))
                .count());
    }

    /**
     * 商家根据用户id已读用户消息
     *
     * @param userId  用户id
     * @param shopId  店铺id
     * @param adminId 店铺管理员id
     */
    @Override
    @Redisson(value = PigeonConst.PIGEON_SEND_USER_MSG_LOCK, key = "#adminId+':'+#shopId+':'+#userId")
    public void readUserMessagesByUserId(Long userId, Long shopId, Long adminId) {
        ISystem.shopId(shopId,
                () -> messageService.lambdaUpdate()
                        .set(Message::getRead, Boolean.TRUE)
                        .set(Message::getAdminId, adminId)
                        .eq(Message::getSenderType, UserType.CONSUMER)
                        .eq(Message::getReceiverType, UserType.SHOP_ADMIN)
                        .eq(Message::getUserId, userId)
                        .eq(Message::getRead, Boolean.FALSE)
                        .and(aq -> aq.eq(Message::getAdminId, adminId).or(orQw -> orQw.isNull(Message::getAdminId))
                        )
                        .update()
        );
    }

    /**
     * 平台发送消息给用户
     *
     * @param userId      用户id
     * @param chatMessage 消息体
     */
    @Override
    @Redisson(value = PigeonConst.PIGEON_SEND_USER_MSG_LOCK, key = "#userId")
    public void sendMessageToUser(Long userId, ChatMessage chatMessage) {
        SecureUser user = ISecurity.userMust();
        Long shopId = user.getShopId();
        Long adminId = user.getId();
        menageSendMessageCommonLogic(shopId, adminId, userId, chatMessage, UserType.PLATFORM_ADMIN);
    }


    private void menageSendMessageCommonLogic(Long shopId, Long adminId, Long userId, ChatMessage chatMessage, UserType userType) {
        CompletableTask.getOrThrowException(
                CompletableTask.allOf(
                        globalExecutor,
                        //检查店铺管理员资料
                        () -> shopAndUserService.checkAndGetMessageShopAdmin(shopId, adminId),
                        //检查用户资料
                        () -> shopAndUserService.checkAndGetMessageUser(userId)
                )
        );
        //消息资料
        Message message = new Message()
                .setShopId(shopId)
                .setAdminId(adminId)
                .setUserId(userId)
                .setSenderType(userType)
                .setReceiverType(UserType.CONSUMER)
                .setMessageType(chatMessage.getMessageType())
                .setMessage(StrUtil.trim(chatMessage.getMessage()))
                .setRead(Boolean.FALSE);
        messageService.save(message);
        //设置自己接管该客服消息 并设置为已读
        messageService.lambdaUpdate()
                .set(Message::getAdminId, adminId)
                .set(Message::getRead, Boolean.TRUE)
                .ne(Message::getSenderType, userType)
                .eq(Message::getUserId, userId)
                .and(
                        qw -> qw.eq(Message::getRead, Boolean.FALSE).or(orQw -> orQw.isNull(Message::getAdminId))
                )
                .update();
        CompletableTask.allOf(
                globalExecutor,
                //发送 消息提醒
                () -> messageSender.send(
                        SendType.MARKED_USER.getDestination(0L, userId),
                        new CustomServiceMessage()
                                .setShopId(shopId)
                                .setMessageId(message.getId())
                                .setSenderType(message.getSenderType())
                                .setSenderId(adminId)
                                .setReceiverType(message.getReceiverType())
                                .setReceiverId(userId)
                                .setMessageType(message.getMessageType())
                                .setMessage(message.getMessage())
                )
        );
    }


}

package com.medusa.gruul.carrier.pigeon.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.carrier.pigeon.api.enums.MessageType;
import com.medusa.gruul.carrier.pigeon.api.enums.SendType;
import com.medusa.gruul.carrier.pigeon.api.enums.UserType;
import com.medusa.gruul.carrier.pigeon.api.model.CustomServiceMessage;
import com.medusa.gruul.carrier.pigeon.service.im.repository.PlatformShopAndUserMessageRepository;
import com.medusa.gruul.carrier.pigeon.service.model.PigeonConst;
import com.medusa.gruul.carrier.pigeon.service.model.dto.ChatMessage;
import com.medusa.gruul.carrier.pigeon.service.model.dto.MessageShopPageQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.model.vo.MessageShopVO;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.Message;
import com.medusa.gruul.carrier.pigeon.service.mp.service.IMessageService;
import com.medusa.gruul.carrier.pigeon.service.mp.service.IMessageShopService;
import com.medusa.gruul.carrier.pigeon.service.mp.service.UnShownUserMessageService;
import com.medusa.gruul.carrier.pigeon.service.service.MessageSender;
import com.medusa.gruul.carrier.pigeon.service.service.ShopAndUserService;
import com.medusa.gruul.carrier.pigeon.service.service.UserMessageService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.helper.CompletableTask;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author 张治保
 * date 2022/10/14
 */
@Service
@RequiredArgsConstructor
public class UserMessageServiceImpl implements UserMessageService {

    private final IMessageService messageService;
    private final ShopAndUserService shopAndUserService;
    private final IMessageShopService messageShopService;
    private final Executor globalExecutor;
    private final MessageSender messageSender;
    private final UnShownUserMessageService unShownUserMessageService;

    private final PlatformShopAndUserMessageRepository platformShopAndUserMessageRepository;

    @Override
    public IPage<MessageShopVO> messageShopPage(MessageShopPageQueryDTO query) {
        query.setUserId(ISecurity.userMust().getId());
        return TenantShop.disable(() -> messageShopService.messageShopPage(query));
    }


    @Override
    @Redisson(value = PigeonConst.PIGEON_SEND_ADMIN_MSG_LOCK, key = "#userId+':'+#shopId")
    public void readMessages(Long userId, Long shopId) {
        ISystem.shopId(shopId,
                () -> messageService.lambdaUpdate()
                        .set(Message::getRead, Boolean.TRUE)
                        .eq(Message::getUserId, userId)
                        .eq(Message::getSenderType, UserType.SHOP_ADMIN)
                        .eq(Message::getRead, Boolean.FALSE)
                        .update()
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = PigeonConst.PIGEON_SEND_ADMIN_MSG_LOCK, key = "#userId+':'+#shopId")
    public void sendMessage(Long userId, Long shopId, ChatMessage chatMessage) {
        shopAndUserService.checkAndGetMessageShop(shopId);
        sendMessageCommonLogic(userId, shopId, chatMessage, UserType.SHOP_ADMIN);
    }

    @Override
    public Long getUnreadMessageCountByUserId(Long userId) {
        return TenantShop.disable(() -> messageService.lambdaQuery()
                .eq(Message::getUserId, userId)
                .eq(Message::getRead, Boolean.FALSE)
                .ne(Message::getShopId, CommonPool.NUMBER_ZERO)
                .ne(Message::getSenderType, UserType.CONSUMER)
                .count());
    }

    @Override
    public void sendMessageToPlatform(Long userId, ChatMessage chatMessage) {
        long shopId = CommonPool.NUMBER_ZERO;
        if (chatMessage.getMessageType() != MessageType.TEXT && chatMessage.getMessageType() != MessageType.IMAGE) {
            throw new GlobalException("平台客服不支持除文本以及图片形式以外的内容");
        }
        sendMessageCommonLogic(userId, shopId, chatMessage, UserType.PLATFORM_ADMIN);
    }


    private void sendMessageCommonLogic(Long userId, Long shopId, ChatMessage chatMessage, UserType receiverType) {
        CompletableFuture<Option<Long>> lastMessageAdminIdFuture = CompletableFuture.supplyAsync(
                () -> shopAndUserService.getLastMessageAdminId(shopId, userId),
                globalExecutor
        );

        CompletableTask.getOrThrowException(
                CompletableTask.allOf(
                        lastMessageAdminIdFuture,
                        // 检查用户资料
                        CompletableFuture.runAsync(() -> shopAndUserService.checkAndGetMessageUser(userId), globalExecutor)
                )
        );

        //消息资料
        Option<Long> lastMessageAdminId = CompletableTask.getOrThrowException(lastMessageAdminIdFuture);
        Long receiverId = lastMessageAdminId.getOrNull();
        Message message = new Message()
                .setShopId(shopId)
                .setAdminId(receiverId)
                .setUserId(userId)
                .setSenderType(UserType.CONSUMER)
                .setReceiverType(receiverType)
                .setMessageType(chatMessage.getMessageType())
                .setMessage(StrUtil.trim(chatMessage.getMessage()))
                .setRead(Boolean.FALSE);
        messageService.save(message);
        SendType attemptRequest = shopId == CommonPool.NUMBER_ZERO ? SendType.MARKED_PLATFORM : SendType.MARKED_SHOP;
        SendType unexpectedRequest = attemptRequest == SendType.MARKED_PLATFORM ? SendType.MARKED_PLATFORM_ADMIN : SendType.MARKED_SHOP_ADMIN;
        CompletableTask.allOf(
                globalExecutor,
                // 发送消息
                () -> messageSender.send(
                        lastMessageAdminId
                                .map(adminId -> unexpectedRequest)
                                .getOrElse(() -> attemptRequest)
                                .getDestination(shopId, lastMessageAdminId.getOrNull()),
                        new CustomServiceMessage()
                                .setShopId(shopId)
                                .setMessageId(message.getId())
                                .setSenderType(message.getSenderType())
                                .setSenderId(userId)
                                .setReceiverType(message.getReceiverType())
                                .setReceiverId(receiverId)
                                .setMessageType(message.getMessageType())
                                .setMessage(message.getMessage())
                ),
                // 未读消息设置为已读
                () -> this.readMessages(userId, shopId),
                // 修改receiverType删除未展示状态
                () -> unShownUserMessageService.showDeleteMessage(userId, shopId, UserType.CONSUMER, receiverType)
        );
    }
}

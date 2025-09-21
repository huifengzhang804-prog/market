package com.medusa.gruul.carrier.pigeon.service.im.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.carrier.pigeon.api.enums.SendType;
import com.medusa.gruul.carrier.pigeon.api.enums.UserType;
import com.medusa.gruul.carrier.pigeon.service.im.entity.*;
import com.medusa.gruul.carrier.pigeon.service.im.model.dto.PlatformShopAndUserMessageDTO;
import com.medusa.gruul.carrier.pigeon.service.im.model.dto.PlatformShopAndUserPageQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.im.model.dto.PlatformShopAndUserSingeRoomQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.im.model.vo.PlatformShopAndUserMultiChatRoomVO;
import com.medusa.gruul.carrier.pigeon.service.im.repository.PlatformShopAndUserChatRoomRepository;
import com.medusa.gruul.carrier.pigeon.service.im.repository.PlatformShopAndUserMessageRepository;
import com.medusa.gruul.carrier.pigeon.service.im.service.CarrierUserService;
import com.medusa.gruul.carrier.pigeon.service.im.service.IPlatformShopAndUserChatMessageService;
import com.medusa.gruul.carrier.pigeon.service.im.service.IShopService;
import com.medusa.gruul.carrier.pigeon.service.service.MessageSender;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.helper.CompletableTask;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.medusa.gruul.carrier.pigeon.api.enums.ChatWithType.CONSUMER;

/**
 * 平台/店铺-用户聊天接口实现类,参考{@link IPlatformShopAndUserChatMessageService}</p>
 *
 * @author An.Yan
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PlatformShopAndUserChatMessageImpl implements IPlatformShopAndUserChatMessageService {

    private static final Pattern ROOM_ID_PATTERN = Pattern.compile("^\\d+:\\d+$");
    private final PlatformShopAndUserChatRoomRepository chatRoomRepository;
    private final PlatformShopAndUserMessageRepository messageRepository;
    private final IShopService shopService;
    private final CarrierUserService carrierUserService;
    private final Executor globalExecutor;
    private final MessageSender messageSender;
    private final ShopRpcService shopRpcService;

    /**
     * 平台/店铺-用户创建聊天室
     *
     * @param shopId 店铺Id
     * @param userId 用户ID
     */
    @Override
    public void createRoom(Long shopId, Long userId) {
        // 检查店铺&User
        if (CommonPool.NUMBER_ZERO != shopId) {
            shopService.checkAndGetMessageShop(shopId);
        }
        carrierUserService.checkAndGetUserInfo(userId);
        // 创建聊天室
        Long currTime = System.currentTimeMillis();
        chatRoomRepository.createRoom(new PlatformShopAndUserChatRoom(shopId, userId, currTime));
        chatRoomRepository.createRoom(new PlatformShopAndUserChatRoom(userId, shopId, currTime));
    }

    /**
     * 分页查询平台/店铺与C端用户聊天室列表
     *
     * @param dto {@link PlatformShopAndUserPageQueryDTO}
     * @return {@link PlatformShopAndUserMultiChatRoomVO}
     */
    @Override
    public IPage<PlatformShopAndUserMultiChatRoomVO> listMultiChatRoom(PlatformShopAndUserPageQueryDTO dto) {
        IPage<PlatformShopAndUserMultiChatRoomVO> result = new Page<>();
        // 根据client type 创建ChatRoom对象
        ClientType clientType = ISystem.clientTypeMust();
        PlatformShopAndUserChatRoom chatRoom;
        switch (clientType) {
            case SHOP_CONSOLE, PLATFORM_CONSOLE ->
                    chatRoom = new PlatformShopAndUserChatRoom(ISecurity.userMust().getShopId());
            case CONSUMER -> chatRoom = new PlatformShopAndUserChatRoom(dto.getUserId());
            default -> throw new GlobalException("无效的Client-Type");
        }
        // 如果非平台端 则检查店铺信息是否正常
        if (!ISecurity.anyRole(Roles.SUPER_ADMIN, Roles.SUPER_CUSTOM_ADMIN) && dto.getShopId() != null) {
            shopService.checkAndGetMessageShop(dto.getShopId());
        }


        // 根据分页信息获取聊天室信息
        Set<String> chatRoomSet = chatRoomRepository.listMultiChatRoom(chatRoom, dto);
        List<PlatformShopAndUserMultiChatRoomVO> chatRoomHolder = new ArrayList<>();

        // 遍历聊天室Set,设置每个聊天室最后一条消息
        Long chatId = dto.getShopId() == null ? dto.getUserId() : dto.getShopId();
        chatRoomSet.stream()
                // 通过正则表达式筛选符合特定模式的房间 ID 作进一步处理
                .filter(e -> ROOM_ID_PATTERN.matcher(e).matches())
                // 对筛选后的每个元素执行后面的行为
                .forEach(item -> {
                    // 将标记项以冒号分隔后取第一个元素并转换为 Long 类型作为 fromId
                    Long fromId = Long.parseLong(item.split(StrUtil.COLON)[CommonPool.NUMBER_ZERO]);
                    // 将标记项以冒号分隔后取第二个元素并转换为 Long 类型作为 toId
                    Long toId = Long.parseLong(item.split(StrUtil.COLON)[CommonPool.NUMBER_ONE]);
                    // 根据 chatId 的值来确定与谁聊天的 ID，如果 chatId 等于 fromId，则取 toId，否则取 fromId
                    Long chatWithId = chatId.longValue() == fromId.longValue() ? toId : fromId;

                    // 如果是C端用户在和店铺聊天,排除掉平台消息
                    if (CONSUMER.equals(dto.getChatWithType()) && chatWithId == CommonPool.NUMBER_ZERO) {
                        return;
                    }

                    // 获取聊天室最后一条消息
                    PlatformShopAndUserChatRoom currChatRoom = new PlatformShopAndUserChatRoom(fromId, toId, null);
                    Set<String> messageSet = messageRepository.listChatMessageByRoomId(currChatRoom, CommonPool.NUMBER_ZERO, CommonPool.NUMBER_ONE);

                    // 根据chat with type设置聊天属性
                    PlatformShopAndUserMultiChatRoomVO chatRoomVO;
                    if (CONSUMER.equals(dto.getChatWithType())) {
                        chatRoomVO = new PlatformShopAndUserMultiChatRoomVO()
                                .setChatWithShopInfo(shopService.checkAndGetMessageShop(chatWithId));
                        // keywords模糊匹配群聊名称
                        if (StringUtils.isNotEmpty(dto.getKeywords()) &&
                                !chatRoomVO.getChatWithShopInfo().getShopName().contains(dto.getKeywords())) {
                            return;
                        }
                    } else {

                        chatRoomVO = new PlatformShopAndUserMultiChatRoomVO()
                                .setChatWithUserInfo(carrierUserService.checkAndGetUserInfo(chatWithId));
                        // keywords模糊匹配群聊名称
                        if (StringUtils.isNotEmpty(dto.getKeywords()) &&
                                !chatRoomVO.getChatWithUserInfo().getNickname().contains(dto.getKeywords())) {
                            return;
                        }
                    }

                    // 设置lastMessage
                    if (CollUtil.isNotEmpty(messageSet)) {
                        ChatMessage chatMessage = JSON.parseObject(messageSet.stream().findFirst().get(), ChatMessage.class);
                        ChatMessageSender sender = chatMessage.getSender();
                        UserType userType = sender.getSenderType();
                        if (UserType.PLATFORM_ADMIN.equals(userType)) {
                            //这条消息 如果是平台发送的消息 现在又是平台来获取消息 则显示消息已读
                            chatMessage.setRead(Boolean.TRUE);
                        } else if ((UserType.SHOP_ADMIN.equals(userType) || UserType.SUPPLIER_ADMIN.equals(userType)) && ISystem.clientTypeMust() != ClientType.CONSUMER) {
                            //消息是店铺或者供应商自己发的 现在 店铺/供应商又来获取这个消息 则显示消息已读
                            ShopInfo senderShopInfo = sender.getSenderShopInfo();
                            if (senderShopInfo.getShopId().equals(String.valueOf(ISecurity.userMust().getShopId()))) {
                                chatMessage.setRead(Boolean.TRUE);
                            }
                        } else if (UserType.CONSUMER.equals(userType) && ISystem.clientTypeMust() == ClientType.CONSUMER) {
                            // 如果最后一条消息是用户端用户发送 设置用户返回消息为已读
                            chatMessage.setRead(Boolean.TRUE);
                        }
                        chatRoomHolder.add(chatRoomVO.setLastMessage(chatMessage));
                    } else {
                        //只有聊天室 没有消息
                        chatRoomHolder.add(chatRoomVO);
                    }
                });
        // 获取查询消息 的用户id
        Set<Long> userIds = chatRoomHolder.stream()
                .map(PlatformShopAndUserMultiChatRoomVO::getChatWithUserInfo)
                .filter(Objects::nonNull)
                .map(UserInfo::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        //非用户端进行会员权益查询
        if (clientType != ClientType.CONSUMER && CollUtil.isNotEmpty(userIds)) {

            // 根据userId 获取是否包含专属客服权益  map<userId,boolean(是否包含专属会员权益)>
            Map<Long, Boolean> userMap = carrierUserService.checkUserRights(userIds);

            chatRoomHolder.stream().map(PlatformShopAndUserMultiChatRoomVO::getChatWithUserInfo).forEach(chatWithUserInfo -> {
                chatWithUserInfo.setIncludeRights(userMap.get(chatWithUserInfo.getUserId()));
            });
        }

        result.setRecords(chatRoomHolder);
        return result;
    }

    /**
     * 获取平台/店铺-用户聊天室聊天记录
     *
     * @param dto {@link PlatformShopAndUserSingeRoomQueryDTO}
     * @return {@link ChatMessage}
     */
    @Override
    public IPage<ChatMessage> listSingleChatRoomMessages(PlatformShopAndUserSingeRoomQueryDTO dto) {
        // 根据ClientType判断客户端类型
        PlatformShopAndUserChatRoom chatRoom = buildChatRoom(dto.getShopId(), dto.getUserId());
        return this.messageRepository.listMessageByRoomId(chatRoom, dto);
    }

    private PlatformShopAndUserChatRoom buildChatRoom(Long shopId, Long userId) {
        ClientType clientType = ISystem.clientTypeMust();
        PlatformShopAndUserChatRoom chatRoom;
        switch (clientType) {
            case SHOP_CONSOLE, CONSUMER -> chatRoom = new PlatformShopAndUserChatRoom(shopId, userId, null);
            case PLATFORM_CONSOLE -> chatRoom = new PlatformShopAndUserChatRoom(0L, userId, null);
            default -> throw new GlobalException("无效的Client-Type");
        }
        return chatRoom;
    }

    /**
     * 平台/店铺-用户发送消息
     *
     * @param dto {@link PlatformShopAndUserMessageDTO}
     */
    @Override
    public void sendMessage(PlatformShopAndUserMessageDTO dto) {
        Long currTime = System.currentTimeMillis();

        // 根据client type组装对象
        ClientType clientType = ISystem.clientTypeMust();
        Long shopId, userId;
        PlatformShopAndUserChatRoom senderChatRoom, receiverChatRoom;
        ChatMessageSender chatMessageSender = new ChatMessageSender();
        ChatMessageReceiver chatMessageReceiver = new ChatMessageReceiver();
        String destinationTopic, destinationTopic2 = null;
        switch (clientType) {
            case SHOP_CONSOLE -> {
                shopId = dto.getSenderId();
                userId = dto.getReceiverId();
                senderChatRoom = new PlatformShopAndUserChatRoom(shopId, userId, currTime);
                receiverChatRoom = senderChatRoom.reverse();
                chatMessageSender.setSenderType(UserType.SHOP_ADMIN).setSenderShopInfo(getShopInfo(shopId));
                chatMessageReceiver.setReceiverType(UserType.CONSUMER).setReceiverUserInfo(getUserInfo(userId));
                destinationTopic = SendType.H5_USER.getDestination((long) CommonPool.NUMBER_ZERO, userId);
                destinationTopic2 = SendType.MARKED_SHOP.getDestination(shopId);
            }
            case PLATFORM_CONSOLE -> {
                shopId = dto.getSenderId();
                userId = dto.getReceiverId();
                senderChatRoom = new PlatformShopAndUserChatRoom(0L, userId, currTime);
                receiverChatRoom = senderChatRoom.reverse();
                chatMessageSender.setSenderType(UserType.PLATFORM_ADMIN).setSenderShopInfo(getShopInfo(shopId));
                chatMessageReceiver.setReceiverType(UserType.CONSUMER).setReceiverUserInfo(getUserInfo(userId));
                destinationTopic = SendType.H5_USER.getDestination((long) CommonPool.NUMBER_ZERO, userId);
                destinationTopic2 = SendType.MARKED_PLATFORM.getDestination(shopId);
            }
            case CONSUMER -> {
                shopId = dto.getReceiverId();
                userId = dto.getSenderId();
                //店铺客服聊天的时候，校验店铺状态
                if (CommonPool.NUMBER_ZERO != shopId) {
                    checkShopStatus(shopId);
                }
                senderChatRoom = new PlatformShopAndUserChatRoom(shopId, userId, currTime);
                receiverChatRoom = senderChatRoom.reverse();
                chatMessageSender.setSenderType(UserType.CONSUMER).setSenderUserInfo(getUserInfo(userId));
                chatMessageReceiver.setReceiverType(UserType.SHOP_ADMIN).setReceiverShopInfo(getShopInfo(shopId));
                if (shopId != CommonPool.NUMBER_ZERO) {
                    destinationTopic = SendType.MARKED_SHOP.getDestination(shopId);
                    //destinationTopic2 = SendType.MARKED_SHOP.getDestination(shopId);
                } else {
                    destinationTopic = SendType.MARKED_PLATFORM.getDestination(shopId);
                    //destinationTopic2 = SendType.MARKED_PLATFORM.getDestination(shopId);
                }
            }
            default -> throw new GlobalException("无效的Client-Type");
        }

        // 检查聊天室
        if (!this.chatRoomRepository.checkChatRoomExist(senderChatRoom) ||
                !this.chatRoomRepository.checkChatRoomExist(receiverChatRoom)) {
            return;
        }

        ChatMessage chatMessage = new ChatMessage()
                .setMessage(StrUtil.trim(dto.getContent()))
                .setMessageType(dto.getMessageType())
                .setRead(Boolean.FALSE)
                .setShow(Boolean.TRUE)
                .setHandled(Boolean.FALSE)
                .setSendTime(currTime)
                .setSender(chatMessageSender)
                .setReceiver(chatMessageReceiver);

        // 发送消息
        this.messageRepository.sendMessage(senderChatRoom, chatMessage);

        // 更新聊天室
        this.chatRoomRepository.updateRoom(senderChatRoom);
//        this.chatRoomRepository.updateRoom(senderChatRoom);

        // 异步发送到WS
        String finalDestinationTopic = destinationTopic;
        String finalDestinationTopic2 = destinationTopic2;
        CompletableTask.allOf(
                globalExecutor,
                () -> messageSender.send(
                        finalDestinationTopic,
                        new PlatformShopAndUserMessage().setSender(chatMessage.getSender())
                                .setSenderTime(currTime)
                                .setRead(Boolean.TRUE)
                                .setReceiver(chatMessage.getReceiver())
                                .setMessage(chatMessage.getMessage())
                                .setMessageType(chatMessage.getMessageType())
                ),
                () -> messageSender.send(
                        finalDestinationTopic2,
                        new PlatformShopAndUserMessage()
                                .setSender(chatMessage.getSender())
                                .setSenderTime(currTime)
                                .setRead(Boolean.TRUE)
                                .setReceiver(chatMessage.getReceiver())
                                .setMessage(chatMessage.getMessage())
                                .setMessageType(chatMessage.getMessageType())
                )
        );
    }

    /**
     * 校验店铺状态
     *
     * @param shopId 店铺id
     */
    private void checkShopStatus(Long shopId) {
        //客户端发送消息的时候，验证下店铺是否删除或不可用
        ShopInfoVO shopInfo = shopRpcService.getShopInfoByShopId(shopId);
        //店铺不存在
        if (null == shopInfo) {
            throw new GlobalException("店铺不存在");
        }
        //店铺不可用
        if (!ShopStatus.NORMAL.equals(shopInfo.getStatus())) {
            throw new GlobalException("店铺不可用");
        }
    }

    @Nullable
    private ShopInfo getShopInfo(Long shopId) {
        return Optional.ofNullable(shopId)
                .map(e -> CommonPool.NUMBER_ZERO == e ?
                        new ShopInfo(String.valueOf(CommonPool.NUMBER_ZERO), "平台", null) :
                        shopService.checkAndGetMessageShop(e))
                .orElse(null);
    }

    @Nullable
    private UserInfo getUserInfo(Long userId) {
        return Optional.ofNullable(userId)
                .map(carrierUserService::checkAndGetUserInfo)
                .orElse(null);
    }
}

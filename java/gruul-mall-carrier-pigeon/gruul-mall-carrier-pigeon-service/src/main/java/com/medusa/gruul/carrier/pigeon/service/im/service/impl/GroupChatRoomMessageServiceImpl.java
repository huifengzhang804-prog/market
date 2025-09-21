package com.medusa.gruul.carrier.pigeon.service.im.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.carrier.pigeon.api.enums.SendType;
import com.medusa.gruul.carrier.pigeon.api.enums.UserType;
import com.medusa.gruul.carrier.pigeon.service.im.entity.*;
import com.medusa.gruul.carrier.pigeon.service.im.model.dto.MessageSupplierShopPageQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.im.model.dto.SingleChatListQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.im.model.vo.ChatRoomVO;
import com.medusa.gruul.carrier.pigeon.service.im.repository.GroupChatRoomRepository;
import com.medusa.gruul.carrier.pigeon.service.im.repository.MessageRepository;
import com.medusa.gruul.carrier.pigeon.service.im.service.CarrierUserService;
import com.medusa.gruul.carrier.pigeon.service.im.service.IGroupChatRoomMessageService;
import com.medusa.gruul.carrier.pigeon.service.im.service.IShopService;
import com.medusa.gruul.carrier.pigeon.service.model.PigeonConst;
import com.medusa.gruul.carrier.pigeon.service.service.MessageSender;
import com.medusa.gruul.carrier.pigeon.service.service.ShopAndUserService;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.helper.CompletableTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.regex.Pattern;

/**
 * <p>供应商-店铺群聊消息接口实现类</p>
 * <p>获取多个聊天室的最近的消息:{@link IGroupChatRoomMessageService#listMultiChatRoom(MessageSupplierShopPageQueryDTO)}</p>
 * <p>获取单个聊天室的消息:{@link IGroupChatRoomMessageService#listSingleChatRoomMessages(SingleChatListQueryDTO)}</p>
 * <p>发送消息:{@link IGroupChatRoomMessageService#sendMessage(ChatMsg)}</p>
 *
 * @author An.Yan
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GroupChatRoomMessageServiceImpl implements IGroupChatRoomMessageService {

    private final GroupChatRoomRepository groupChatRoomRepository;
    private final MessageRepository messageRepository;
    private final ShopAndUserService shopAndUserService;
    private final Executor globalExecutor;
    private final MessageSender messageSender;
    private final IShopService shopService;
    private final CarrierUserService userService;

    /**
     * 根据分页信息,获取当前登陆用户所在店铺的多个聊天室的最近的消息
     *
     * @param queryDTO {@link MessageSupplierShopPageQueryDTO}
     * @return {@link ChatRoomVO}
     */
    @Override
    public IPage<ChatRoomVO> listMultiChatRoom(MessageSupplierShopPageQueryDTO queryDTO) {
        IPage<ChatRoomVO> result = new Page<>();
        SecureUser user = ISecurity.userMust();
        queryDTO.setShopId(user.getShopId());
        queryDTO.setReceiverUserId(user.getId());

        // 根据分页信息获取用户所在店铺的相应的聊天室列表
        GroupChatRoom groupChatRoom = new GroupChatRoom(user.getShopId());
        Set<String> groupChatRoomSet = groupChatRoomRepository.listGroupChatRoom(groupChatRoom, queryDTO);
        List<ChatRoomVO> chatRoomHolderList = new ArrayList<>();
        Pattern roomIdPattern = Pattern.compile("^\\d+:\\d+$");

        // 获取每个聊天室的聊天记录
        groupChatRoomSet.stream().filter(e -> roomIdPattern.matcher(e).matches()).forEach(roomObj -> {
            Long fromShopId = Long.parseLong(roomObj.split(":")[0]);
            Long toShopId = Long.parseLong(roomObj.split(":")[1]);
            GroupChatRoom groupChatRoomItem = new GroupChatRoom(fromShopId, toShopId, null, null);
            Long currShopId = !user.getShopId().equals(fromShopId) ? fromShopId : toShopId;
            ShopInfo shopInfo = shopService.checkAndGetMessageShop(currShopId);
            ChatRoomVO chatRoomVO = new ChatRoomVO().setChatWithShopInfo(shopInfo);
            // keywords模糊匹配群聊名称
            if (StringUtils.isNotEmpty(queryDTO.getKeywords()) &&
                    !chatRoomVO.getChatWithShopInfo().getShopName().contains(queryDTO.getKeywords())) {
                return;
            }
            Set<String> messages = this.messageRepository.listChatMessageByRoomId(groupChatRoomItem, 0, 1);
            if (!CollectionUtils.isEmpty(messages)) {
                ChatMessage chatMessage = JSON.parseObject(messages.stream().findFirst().get(), ChatMessage.class);
                ChatMessageSender sender = chatMessage.getSender();
                UserType senderType = sender.getSenderType();
                if (UserType.SHOP_ADMIN.equals(senderType) || UserType.SUPPLIER_ADMIN.equals(senderType)) {
                    //当前消息的发送者是店铺或者供应商 然后自己又来读这个消息 则设置为已读
                    ShopInfo senderShopInfo = sender.getSenderShopInfo();
                    if (senderShopInfo.getShopId().equals(String.valueOf(ISecurity.userMust().getShopId()))) {
                        chatMessage.setRead(Boolean.TRUE).setShow(Boolean.FALSE);
                    }
                }
                chatRoomVO.setLastMessage(chatMessage);
            }
            chatRoomHolderList.add(chatRoomVO);

        });
        result.setRecords(chatRoomHolderList);
        return result;
    }


    /**
     * 根据分页信息,获取当前登陆用户所在店铺的单个聊天室的聊天记录
     *
     * @param query 分页对象
     * @return {@link ChatMessage}
     */
    @Override
    public IPage<ChatMessage> listSingleChatRoomMessages(SingleChatListQueryDTO query) {
        query.setReceiverShopId(ISecurity.userMust().getShopId());
        query.setReceiverUserId(ISecurity.userMust().getId());
        return messageRepository.listMessageByRoomId(new GroupChatRoom(query.getSenderShopId(), query.getReceiverShopId(), null, null), query);
    }


    /**
     * <p>创建群聊聊天室:</p>
     * <p>1. 检查店铺</p>
     * <p>2. 检查登录用户</p>
     * <p>3. 创建GroupChatRoom</p>
     *
     * @param fromShopId 聊天发起店铺ID
     * @param toShopId   聊天接收店铺ID
     */
    @Override
    public void createGroupChatRoom(Long fromShopId, Long toShopId) {
        shopService.checkAndGetMessageShop(fromShopId);
        shopService.checkAndGetMessageShop(toShopId);
        userService.checkAndGetUserInfo(ISecurity.userMust().getId());
        Long currTime = System.currentTimeMillis();
        LocalDateTime createTime = LocalDateTime.now();
        if (!groupChatRoomRepository.checkRoomExist(new GroupChatRoom(fromShopId, toShopId, createTime, currTime))) {
            groupChatRoomRepository.createGroupRoom(new GroupChatRoom(fromShopId, toShopId, createTime, currTime));
        }
        if (!groupChatRoomRepository.checkRoomExist(new GroupChatRoom(toShopId, fromShopId, createTime, currTime))) {
            groupChatRoomRepository.createGroupRoom(new GroupChatRoom(toShopId, fromShopId, createTime, currTime));
        }

    }


    /**
     * 发送消息
     *
     * @param chatMessage 聊天消息 {@link ChatMsg}
     */
    @Override
    @Redisson(value = PigeonConst.PIGEON_SEND_USER_MSG_LOCK, key = "#userId")
    @Transactional(rollbackFor = Exception.class)
    public void sendMessage(ChatMsg chatMessage) {
        SecureUser user = ISecurity.userMust();
        chatMessage.setSenderUserId(user.getId());
        chatMessage.setSenderShopId(user.getShopId());
        //检查店铺资料
        shopAndUserService.checkAndGetMessageShop(chatMessage.getSenderShopId());
        doSendMessage(chatMessage);
    }

    /**
     * <p>发送消息:</p>
     * <p>1. 检查发送方信息</p>
     * <p>2. 根据{@link ISystem#clientTypeMust()}计算topic</p>
     * <p>3. 持久化消息对象到Redis中,参见{@link GroupChatRoom#getGroupChatRoomId()}</p>
     * <p>4. 异步发送消息</p>
     *
     * @param msg 聊天消息对象
     */
    private void doSendMessage(ChatMsg msg) {
        CompletableTask.getOrThrowException(
                CompletableTask.allOf(
                        globalExecutor,
                        //检查店铺管理员资料
                        () -> shopAndUserService.checkAndGetMessageShopAdmin(msg.getSenderShopId(), msg.getSenderUserId()),
                        //检查用户资料
                        () -> shopAndUserService.checkAndGetMessageUser(msg.getSenderUserId())
                )
        );
        // 根据client type计算destination, senderType, receiverType
        String supplierDestination;
        String shopDestination;
        ClientType clientType = ISystem.clientTypeMust();
        UserType senderType;
        UserType receiverType;
        switch (clientType) {
            case SHOP_CONSOLE -> {
                supplierDestination = SendType.SUPPLIER_SHOP_ADMIN.getDestination(msg.getReceiverShopId());
                shopDestination = SendType.MARKED_SHOP.getDestination(msg.getSenderShopId());
                senderType = UserType.SHOP_ADMIN;
                receiverType = UserType.SUPPLIER_ADMIN;
            }
            case SUPPLIER_CONSOLE -> {
                shopDestination = SendType.MARKED_SHOP.getDestination(msg.getReceiverShopId());
                supplierDestination = SendType.SUPPLIER_SHOP_ADMIN.getDestination(msg.getSenderShopId());
                senderType = UserType.SUPPLIER_ADMIN;
                receiverType = UserType.SHOP_ADMIN;
            }
            default -> throw new GlobalException("无效的Client-Type");
        }

        // 检查UserChatRoom
        Long currTime = System.currentTimeMillis();
        GroupChatRoom senderChatRoom = new GroupChatRoom(msg.getSenderShopId(), msg.getReceiverShopId(), null, currTime);
        GroupChatRoom receiverChatRoom = new GroupChatRoom(msg.getReceiverShopId(), msg.getSenderShopId(), null, currTime);
        if (!groupChatRoomRepository.checkRoomExist(senderChatRoom) || !groupChatRoomRepository.checkRoomExist(receiverChatRoom)) {
            log.warn("聊天室不存在, senderChatRoom = {}, receiverChatRoom = {}",
                    JSON.toJSONString(senderChatRoom), JSON.toJSONString(receiverChatRoom));
            return;
        }

        // 创建消息对象并设置相关属性
        ChatMessage chatMessage = new ChatMessage()
                .setMessage(StrUtil.trim(msg.getContent()))
                .setMessageType(msg.getMessageType())
                .setRead(Boolean.FALSE)
                .setShow(Boolean.TRUE)
                .setHandled(Boolean.FALSE)
                .setSendTime(currTime)
                .setSender(
                        new ChatMessageSender()
                                .setSenderType(senderType)
                                .setSenderShopInfo(shopService.checkAndGetMessageShop(msg.getSenderShopId()))
                                .setSenderUserInfo(userService.checkAndGetUserInfo(msg.getSenderUserId()))
                ).setReceiver(
                        new ChatMessageReceiver()
                                .setReceiverType(receiverType)
                                .setReceiverShopInfo(shopService.checkAndGetMessageShop(msg.getReceiverShopId()))
                );

        // 更新UserChatRoom
        senderChatRoom.setLastUpdateTime(currTime);
        receiverChatRoom.setLastUpdateTime(currTime);
        groupChatRoomRepository.updateRoom(senderChatRoom);
        groupChatRoomRepository.updateRoom(receiverChatRoom);

        // 发送消息到Destination
        messageRepository.sendMessage(new GroupChatRoom(msg.getSenderShopId(), msg.getReceiverShopId(), null, null), chatMessage);

        // 异步发送到WebSocket
        String finalSupplierDestination = supplierDestination;
        String finalShopDestination = shopDestination;
        CompletableTask.allOf(
                globalExecutor,
                () -> messageSender.send(
                        finalSupplierDestination,
                        new SupplierShopMessage().setSender(chatMessage.getSender())
                                .setReceiver(chatMessage.getReceiver())
                                .setMessage(chatMessage.getMessage())
                                .setMessageType(chatMessage.getMessageType())
                ),
                () -> messageSender.send(
                        finalShopDestination,
                        new SupplierShopMessage().setSender(chatMessage.getSender())
                                .setReceiver(chatMessage.getReceiver())
                                .setMessage(chatMessage.getMessage())
                                .setMessageType(chatMessage.getMessageType())
                )
        );
    }
}

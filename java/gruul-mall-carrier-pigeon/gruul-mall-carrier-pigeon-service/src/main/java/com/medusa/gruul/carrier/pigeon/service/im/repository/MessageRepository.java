package com.medusa.gruul.carrier.pigeon.service.im.repository;


import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.carrier.pigeon.api.enums.UserType;
import com.medusa.gruul.carrier.pigeon.service.im.entity.*;
import com.medusa.gruul.carrier.pigeon.service.im.model.dto.SingleChatListQueryDTO;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 基于Redis实现的消息持久层
 *
 * @author An.Yan
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class MessageRepository {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 根据分页信息,获取指定的聊天室的消息记录
     *
     * @param groupChatRoom 聊天室对象,参见{@link GroupChatRoom}
     * @param query         分页对象
     * @return {@link ChatMessage}
     */
    public IPage<ChatMessage> listMessageByRoomId(GroupChatRoom groupChatRoom, SingleChatListQueryDTO query) {
        log.debug("获取群聊天室聊天记录, param = {}", JSON.toJSONString(query));
        Long total = stringRedisTemplate.opsForZSet().size(groupChatRoom.getGroupChatRoomId());
        if (total <= 0) {
            log.warn("聊天室 = {}, 不存在或暂无聊天记录");
            return new Page<>(0, 0);
        }
        long start = (query.getCurrent() - 1) * query.getSize();
        long end = query.getCurrent() * query.getSize();
        IPage<ChatMessage> result = new Page<>(query.getCurrent(), query.getSize(), total);
        Set<String> msgObjects = stringRedisTemplate.opsForZSet().reverseRange(groupChatRoom.getGroupChatRoomId(), start, end);
        result.setRecords(msgObjects
                .stream()
                .map(e -> JSON.parseObject(String.valueOf(e), ChatMessage.class))
                .collect(Collectors.toList()));
        updateMessageRead(groupChatRoom, result.getRecords());
        return result;
    }

    public void updateMessageRead(GroupChatRoom chatRoom, List<ChatMessage> chatMessages) {
        log.warn("用户id {}",ISecurity.userMust().getId());
        List<ChatMessage> readMessages = chatMessages.stream().filter(message -> {
            ChatMessageReceiver receiver = message.getReceiver();
            UserType receiverType = receiver.getReceiverType();
            if (UserType.CONSUMER.equals(receiverType)) {
                //接受者是用户
                UserInfo receiverUserInfo = receiver.getReceiverUserInfo();
                if (receiverUserInfo.getUserId().equals(ISecurity.userMust().getId())) {
                    return Boolean.TRUE;
                } else {
                    return Boolean.FALSE;
                }
            } else if (UserType.SHOP_ADMIN.equals(receiverType) || UserType.PLATFORM_ADMIN.equals(receiverType)) {
                //接受者是店铺 或者供应商
                ShopInfo receiverShopInfo = receiver.getReceiverShopInfo();
                if (receiverShopInfo.getShopId().equals(String.valueOf(ISystem.shopIdMust()))) {
                    return Boolean.TRUE;
                } else {
                    return Boolean.FALSE;
                }
            } else {
                // 系统管理员
                return ISecurity.anyRole(Roles.SUPER_ADMIN);
            }
        }).toList();
        if (CollectionUtils.isEmpty(readMessages)) {
            return;
        }
        log.warn("消息内容 {}" ,readMessages);
        stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
            for (ChatMessage message : readMessages) {
                stringRedisConn.zRem(chatRoom.getGroupChatRoomId(), JSON.toJSONString(message));
                message.setRead(Boolean.TRUE)
                        .setShow(Boolean.FALSE);
                stringRedisConn.zAdd(chatRoom.getGroupChatRoomId(), message.getSendTime(), JSON.toJSONString(message));
            }
            return null;
        });
    }


    /**
     * 获取指定聊天室的消息
     *
     * @param groupChatRoom {@link GroupChatRoom}
     * @param start         消息起始索引
     * @param end           消息结束索引
     * @return {@link Set}
     */
    public Set<String> listChatMessageByRoomId(GroupChatRoom groupChatRoom, int start, int end) {
        return stringRedisTemplate
                .opsForZSet()
                .reverseRange(groupChatRoom.getGroupChatRoomId(), start, end);
    }

    /**
     * 发送消息到聊天室
     *
     * @param destination 聊天室对象
     * @param chatMessage 消息对象
     */
    public void sendMessage(GroupChatRoom destination, ChatMessage chatMessage) {
        stringRedisTemplate
                .opsForZSet()
                .add(
                        destination.getGroupChatRoomId(),
                        JSON.toJSONString(chatMessage),
                        chatMessage.getSendTime()
                );
    }
}

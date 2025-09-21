package com.medusa.gruul.carrier.pigeon.service.im.repository;


import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.carrier.pigeon.api.enums.UserType;
import com.medusa.gruul.carrier.pigeon.service.im.entity.*;
import com.medusa.gruul.carrier.pigeon.service.im.model.dto.PlatformShopAndUserSingeRoomQueryDTO;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.ClientType;
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
 * 基于Redis实现的平台/店铺-用户消息持久层
 *
 * @author An.Yan
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class PlatformShopAndUserMessageRepository {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 获取指定聊天室的消息
     *
     * @param chatRoom {@link PlatformShopAndUserChatRoom}
     * @param start    消息起始索引
     * @param end      消息结束索引
     * @return {@link Set}
     */
    public Set<String> listChatMessageByRoomId(PlatformShopAndUserChatRoom chatRoom, int start, int end) {
        return stringRedisTemplate.opsForZSet().reverseRange(chatRoom.getRoomId(), start, end);
    }

    /**
     * 根据分页信息,获取指定的聊天室的消息记录
     *
     * @param chatRoom 聊天室对象,参见{@link GroupChatRoom}
     * @param dto      分页对象
     * @return {@link ChatMessage}
     */
    public IPage<ChatMessage> listMessageByRoomId(PlatformShopAndUserChatRoom chatRoom, PlatformShopAndUserSingeRoomQueryDTO dto) {
        log.debug("获取群聊天室聊天记录, param = {}", JSON.toJSONString(dto));
        Long total = stringRedisTemplate.opsForZSet().size(chatRoom.getRoomId());
        if (total <= 0) {
            log.warn("聊天室 = {}, 不存在或暂无聊天记录");
            return new Page<>(0, 0);
        }
        long start = (dto.getCurrent() - 1) * dto.getSize();
        long end = dto.getCurrent() * dto.getSize();
        IPage<ChatMessage> result = new Page<>(dto.getCurrent(), dto.getSize(), total);
        Set<String> msgObjects = stringRedisTemplate.opsForZSet().reverseRange(chatRoom.getRoomId(), start, end);
        result.setRecords(msgObjects
                .stream()
                .map(e -> JSON.parseObject(String.valueOf(e), ChatMessage.class))
                .collect(Collectors.toList()));
        this.updateMessageRead(chatRoom, result.getRecords());
        return result;
    }

    /**
     * 批量更新消息已读
     *
     * @param chatRoom     {@link PlatformShopAndUserChatRoom}
     * @param chatMessages {@link  ChatMessage}
     */
    public void updateMessageRead(PlatformShopAndUserChatRoom chatRoom, List<ChatMessage> chatMessages) {
        log.warn("用户id {}", ISecurity.userMust().getId());
        List<ChatMessage> readMessages = chatMessages.stream().filter(message -> {
            ChatMessageReceiver receiver = message.getReceiver();
            UserType receiverType = receiver.getReceiverType();
            if (UserType.CONSUMER.equals(receiverType)) {
                //接受者是用户
                UserInfo receiverUserInfo = receiver.getReceiverUserInfo();
                //TODO 同账号下 userId 相同  要检验读取的用户是否是 移动端用户
                if (receiverUserInfo.getUserId().equals(ISecurity.userMust().getId())
                        && ISystem.clientTypeMust() == ClientType.CONSUMER) {
                    return Boolean.TRUE;
                } else {
                    return Boolean.FALSE;
                }
            } else if (ISecurity.anyRole(Roles.SUPER_ADMIN, Roles.SUPER_CUSTOM_ADMIN)
                    && ISecurity.userMust().getClientType() == ClientType.PLATFORM_CONSOLE) {
                //接收者是平台
                return Boolean.TRUE;
            } else if (ISecurity.anyRole(Roles.ADMIN, Roles.CUSTOM_ADMIN)
                    && ISecurity.userMust().getClientType() == ClientType.SHOP_CONSOLE) {
                //接收者是店铺
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        }).toList();
        if (CollectionUtils.isEmpty(readMessages)) {
            return;
        }
        log.warn("readMessages {}", readMessages);
        stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
            for (ChatMessage message : readMessages) {
                stringRedisConn.zRem(chatRoom.getRoomId(), JSON.toJSONString(message));
                message.setRead(Boolean.TRUE);
                stringRedisConn.zAdd(chatRoom.getRoomId(), message.getSendTime(), JSON.toJSONString(message));
            }
            return null;
        });
    }

    /**
     * 发送消息到指定聊天室
     *
     * @param destination {@link PlatformShopAndUserChatRoom}
     * @param chatMessage {@link ChatMessage}
     */
    public void sendMessage(PlatformShopAndUserChatRoom destination, ChatMessage chatMessage) {
        stringRedisTemplate.opsForZSet()
                .add(destination.getRoomId(), JSON.toJSONString(chatMessage), chatMessage.getSendTime());
    }
}

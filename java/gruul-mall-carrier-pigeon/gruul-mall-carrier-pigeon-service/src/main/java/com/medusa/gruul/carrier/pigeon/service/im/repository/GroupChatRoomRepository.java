package com.medusa.gruul.carrier.pigeon.service.im.repository;


import com.medusa.gruul.carrier.pigeon.service.im.entity.GroupChatRoom;
import com.medusa.gruul.carrier.pigeon.service.im.model.dto.MessageSupplierShopPageQueryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * 群聊聊天室持久层-Redis实现
 * @author An.Yan
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class GroupChatRoomRepository {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 检查聊天室是否存在
     * @param groupChatRoom {@link GroupChatRoom}
     * @return {@link Boolean}
     */
    public Boolean checkRoomExist(GroupChatRoom groupChatRoom) {
        return stringRedisTemplate.opsForZSet()
                .reverseRange(groupChatRoom.getGroupChatRoomKey(), 0, -1)
                .stream()
                .filter(
                        e -> groupChatRoom.getGroupChatRoomValue().equals(e)
                ).findAny().isPresent();
    }

    /**
     * 创建群聊天室,如果其存在则更新.
     * @param groupChatRoom 群聊聊天室对象,参见{@link GroupChatRoom}
     */
    public void createGroupRoom(GroupChatRoom groupChatRoom) {
        stringRedisTemplate.opsForZSet()
                .addIfAbsent(
                        groupChatRoom.getGroupChatRoomKey(),
                        groupChatRoom.getGroupChatRoomValue(),
                        groupChatRoom.getLastUpdateTime()
                );
    }

    /**
     * 根据分页信息,获取由{@link  GroupChatRoom#getGroupChatRoomKey()}指定的聊天室列表
     * @param groupChatRoom 聊天室对象
     * @param query 分页对象
     * @return {@link java.util.HashSet}
     */
    public Set<String> listGroupChatRoom(GroupChatRoom groupChatRoom, MessageSupplierShopPageQueryDTO query) {
        long start = (query.getCurrent() - 1) * query.getSize();
        long end = query.getCurrent() * query.getSize();
        return stringRedisTemplate.opsForZSet()
                .reverseRange(groupChatRoom.getGroupChatRoomKey(), start, end);
    }

    /**
     * 根据分页信息,获取由{@link  GroupChatRoom#getGroupChatRoomKey()}指定的聊天室列表
     * @param groupChatRoom 聊天室对象
     * @return {@link java.util.HashSet}
     */
    public Set<String> listGroupChatRoom(GroupChatRoom groupChatRoom, int start, int end) {
        return stringRedisTemplate.opsForZSet()
                .reverseRange(groupChatRoom.getGroupChatRoomKey(), start, end);
    }

    /**
     * 更新用户聊天室
     * @param groupChatRoom {@link GroupChatRoom}
     * @return {@link Boolean}
     */
    public boolean updateRoom(GroupChatRoom groupChatRoom) {
        return stringRedisTemplate.opsForZSet()
                .add(
                        groupChatRoom.getGroupChatRoomKey(),
                        groupChatRoom.getGroupChatRoomValue(),
                        groupChatRoom.getLastUpdateTime()
                );
    }
}

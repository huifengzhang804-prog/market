package com.medusa.gruul.carrier.pigeon.service.im.repository;

import com.medusa.gruul.carrier.pigeon.service.im.entity.PlatformShopAndUserChatRoom;
import com.medusa.gruul.carrier.pigeon.service.im.model.dto.PlatformShopAndUserPageQueryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class PlatformShopAndUserChatRoomRepository {

    private final StringRedisTemplate stringRedisTemplate;

    public void createRoom(PlatformShopAndUserChatRoom room) {
        stringRedisTemplate
                .opsForZSet()
                .add(room.getRoomKey(), room.getRoomValue(), room.getLastUpdateTime());

    }

    public Set<String> listMultiChatRoom(PlatformShopAndUserChatRoom platformShopAndUserChatRoom, PlatformShopAndUserPageQueryDTO dto) {
        long start = (dto.getCurrent() - 1) * dto.getSize();
        long end = dto.getCurrent() * dto.getSize();
        return stringRedisTemplate.opsForZSet()
                .reverseRange(platformShopAndUserChatRoom.getRoomKey(), start, end);
    }

    public boolean checkChatRoomExist(PlatformShopAndUserChatRoom chatRoom) {
        return stringRedisTemplate.opsForZSet()
                .reverseRange(chatRoom.getRoomKey(), 0, -1)
                .stream()
                .filter(
                        e -> chatRoom.getRoomValue().equals(e)
                )
                .findAny()
                .isPresent();
    }


    /**
     * 更新用户聊天室
     * @param chatRoom {@link PlatformShopAndUserChatRoom}
     * @return {@link Boolean}
     */
    public boolean updateRoom(PlatformShopAndUserChatRoom chatRoom) {
        return stringRedisTemplate.opsForZSet()
                .add(
                        chatRoom.getRoomKey(),
                        chatRoom.getRoomValue(),
                        chatRoom.getLastUpdateTime()
                );
    }
}

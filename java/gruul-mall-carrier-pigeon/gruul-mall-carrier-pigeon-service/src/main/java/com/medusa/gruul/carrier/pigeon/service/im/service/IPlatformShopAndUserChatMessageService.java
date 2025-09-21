package com.medusa.gruul.carrier.pigeon.service.im.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.carrier.pigeon.service.im.entity.ChatMessage;
import com.medusa.gruul.carrier.pigeon.service.im.model.dto.PlatformShopAndUserMessageDTO;
import com.medusa.gruul.carrier.pigeon.service.im.model.dto.PlatformShopAndUserPageQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.im.model.dto.PlatformShopAndUserSingeRoomQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.im.model.vo.PlatformShopAndUserMultiChatRoomVO;

/**
 * <p>平台/店铺-用户聊天接口</p>
 * <p>创建聊天室:{@link IPlatformShopAndUserChatMessageService#createRoom(Long, Long)}</p>
 * <p>分页获取聊天室列表:{@link IPlatformShopAndUserChatMessageService#listMultiChatRoom(PlatformShopAndUserPageQueryDTO)}</p>
 * <p>分页获取单个聊天室聊天记录:{@link IPlatformShopAndUserChatMessageService#listSingleChatRoomMessages(PlatformShopAndUserSingeRoomQueryDTO)}(Long, Long)}</p>
 * <p>发送消息:{@link IPlatformShopAndUserChatMessageService#sendMessage(PlatformShopAndUserMessageDTO)}</p>
 * @author An.Yan
 */
public interface IPlatformShopAndUserChatMessageService {

    /**
     * (平台/店铺) --用户创建聊天室
     * @param shopId 店铺Id
     * @param userId 用户ID
     */
    void createRoom(Long shopId, Long userId);

    /**
     * 分页查询(平台/店铺) --用户聊天室列表
     * @param dto {@link PlatformShopAndUserPageQueryDTO}
     * @return {@link PlatformShopAndUserMultiChatRoomVO}
     */
    IPage<PlatformShopAndUserMultiChatRoomVO> listMultiChatRoom(PlatformShopAndUserPageQueryDTO dto);

    /**
     * (平台/店铺) --用户聊天室聊天记录
     * @param dto {@link PlatformShopAndUserSingeRoomQueryDTO}
     * @return {@link ChatMessage}
     */
    IPage<ChatMessage> listSingleChatRoomMessages(PlatformShopAndUserSingeRoomQueryDTO dto);

    /**
     * (平台/店铺) --用户发送消息
     * @param dto {@link PlatformShopAndUserMessageDTO}
     */
    void sendMessage(PlatformShopAndUserMessageDTO dto);
}

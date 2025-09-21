package com.medusa.gruul.carrier.pigeon.service.im.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.carrier.pigeon.service.im.entity.ChatMessage;
import com.medusa.gruul.carrier.pigeon.service.im.entity.ChatMsg;
import com.medusa.gruul.carrier.pigeon.service.im.model.dto.MessageSupplierShopPageQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.im.model.dto.SingleChatListQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.im.model.vo.ChatRoomVO;

/**
 * <p>群聊消息接口,目前用于供应商-店铺之间的群聊</p>
 * <p>{@link IGroupChatRoomMessageService#listMultiChatRoom(MessageSupplierShopPageQueryDTO)}</p>
 * <p>{@link IGroupChatRoomMessageService#listSingleChatRoomMessages(SingleChatListQueryDTO)}</p>
 * @author An.Yan
 */
public interface IGroupChatRoomMessageService {


    /**
     * 根据分页信息,获取当前登陆用户所在店铺的多个聊天室的最近的消息
     * @param queryDTO {@link MessageSupplierShopPageQueryDTO}
     * @return {@link ChatRoomVO}
     */
    IPage<ChatRoomVO> listMultiChatRoom(MessageSupplierShopPageQueryDTO queryDTO);


    /**
     * 根据分页信息,获取当前登陆用户所在店铺的单个聊天室的聊天记录
     * @param query 分页对象
     * @return {@link ChatMessage}
     */
    IPage<ChatMessage> listSingleChatRoomMessages(SingleChatListQueryDTO query);

    /**
     * <p>创建群聊聊天室:</p>
     * <p>1. 检查店铺</p>
     * <p>2. 检查登录用户</p>
     * <p>3. 创建GroupChatRoom</p>
     * @param fromShopId 聊天发起店铺ID
     * @param toShopId 聊天接收店铺ID
     */
    void createGroupChatRoom(Long fromShopId, Long toShopId);

    /**
     * 发送消息
     *
     * @param chatMessage 聊天消息 {@link ChatMsg}
     */
    void sendMessage(ChatMsg chatMessage);

}

package com.medusa.gruul.carrier.pigeon.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.carrier.pigeon.service.model.dto.ChatMessage;
import com.medusa.gruul.carrier.pigeon.service.model.dto.MessageShopPageQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.model.vo.MessageShopVO;

/**
 * 用户消息服务
 *
 * @author 张治保
 * date 2022/10/14
 */
public interface UserMessageService {

    /**
     * 用户分页查询 消息页  店铺列表
     *
     * @param query 查询条件
     * @return 分页查询结果
     */
    IPage<MessageShopVO> messageShopPage(MessageShopPageQueryDTO query);


    /**
     * 店铺未读消息设置为已读
     *
     * @param userId 用户id
     * @param shopId 店铺id
     */
    void readMessages(Long userId, Long shopId);

    /**
     * 用户发送消息给管理端
     *
     * @param userId      用户id
     * @param shopId      店铺id
     * @param chatMessage 消息内容
     */
    void sendMessage(Long userId, Long shopId, ChatMessage chatMessage);

    /**
     * 查询未读消息的条数
     *
     * @param userId 用户id
     * @return 条数
     */
    Long getUnreadMessageCountByUserId(Long userId);


    /**
     * 用户发送消息到平台
     *
     * @param userId  用户id
     * @param chatMessage 消息内容
     */
    void sendMessageToPlatform(Long userId, ChatMessage chatMessage);
}

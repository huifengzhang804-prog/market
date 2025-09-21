package com.medusa.gruul.carrier.pigeon.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.carrier.pigeon.api.enums.UserType;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.UnShownUserMessage;
import com.medusa.gruul.carrier.pigeon.service.mp.mapper.UnShownUserMessageMapper;
import com.medusa.gruul.carrier.pigeon.service.mp.service.UnShownUserMessageService;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.global.model.exception.GlobalException;
import org.springframework.stereotype.Service;

/**
 * @author miskw
 * @date 2023/3/1
 */
@Service
public class UnShownUserMessageServiceImpl extends ServiceImpl<UnShownUserMessageMapper, UnShownUserMessage> implements UnShownUserMessageService {
    /**
     * 删除消息列表消息
     *
     * @param shopId 店铺id
     * @param adminId 店铺管理员id
     * @param userId 用户id
     * @param senderType 消息发送方
     * @param receiverType 消息接收方
     */
    @Override
    public void deleteMessage(Long shopId,Long adminId, Long userId,UserType senderType,UserType receiverType) {
        UnShownUserMessage unShownUserMessage = ISystem.shopId(shopId, () ->
                this.lambdaQuery()
                        .eq(UnShownUserMessage::getUserId, userId)
                        .eq(UnShownUserMessage::getAdminId,adminId)
                        .eq(UnShownUserMessage::getShopId, shopId)
                        .eq(UnShownUserMessage::getSenderType, senderType)
                        .eq(UnShownUserMessage::getReceiverType, receiverType)
                        .one()
        );
        if (unShownUserMessage != null) {
            return;
        }
        UnShownUserMessage shownUserMessage = new UnShownUserMessage();
        shownUserMessage
                .setAdminId(adminId)
                .setSenderType(UserType.CONSUMER)
                .setReceiverType(UserType.SHOP_ADMIN)
                .setShopId(shopId)
                .setUserId(userId);

        TenantShop.disable(() -> {
                    if (!save(shownUserMessage)) {
                        throw new GlobalException(SystemCode.DATA_ADD_FAILED);
                    }
                }
        );
    }

    /**
     * 消息列表展示
     *
     * @param userId   用户id
     * @param shopId   店铺id
     * @param consumer 消息发送者类型
     * @param receiver 消息接收者类型
     */
    @Override
    public void showDeleteMessage(Long userId, Long shopId, UserType consumer, UserType receiver) {
        Boolean isExists = ISystem.shopId(shopId, () ->
                this.lambdaQuery()
                        .eq(UnShownUserMessage::getUserId, userId)
                        .eq(UnShownUserMessage::getSenderType, consumer)
                        .eq(UnShownUserMessage::getReceiverType, receiver)
                        .exists()
        );
        if (!isExists) {
            return;
        }
        Boolean isRemove = ISystem.shopId(shopId, () ->
                this.lambdaUpdate()
                        .eq(UnShownUserMessage::getUserId, userId)
                        .eq(UnShownUserMessage::getSenderType, consumer)
                        .eq(UnShownUserMessage::getReceiverType, receiver)
                        .remove()
        );
        if (!isRemove) {
            throw new GlobalException(SystemCode.DATA_DELETE_FAILED);
        }
    }
}

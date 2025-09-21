package com.medusa.gruul.carrier.pigeon.service.model.vo;

import com.medusa.gruul.carrier.pigeon.service.mp.entity.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * date 2022/10/11
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class MessageAndShopAdminVO extends Message {


    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickname;

    public MessageAndShopAdminVO setBaseMessage(Message message) {
        this.setShopId(message.getShopId())
                .setAdminId(message.getAdminId())
                .setUserId(message.getUserId())
                .setSenderType(message.getSenderType())
                .setReceiverType(message.getReceiverType())
                .setMessageType(message.getMessageType())
                .setMessage(message.getMessage())
                .setRead(message.getRead())
                .setId(message.getId())
                .setCreateTime(message.getCreateTime());
        return this;
    }
}

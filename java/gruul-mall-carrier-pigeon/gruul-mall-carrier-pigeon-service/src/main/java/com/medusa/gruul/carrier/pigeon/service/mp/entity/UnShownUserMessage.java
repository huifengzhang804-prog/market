package com.medusa.gruul.carrier.pigeon.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.carrier.pigeon.api.enums.UserType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author miskw
 * @date 2023/3/1
 * 商家聊天界面不展示的用户
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_unshown_user_message")
public class UnShownUserMessage extends BaseEntity {
    /**
     * 店铺管理员id
     */
    private Long adminId;

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 店铺id
     */
    private Long shopId;
    /**
     * 消息发送者类型
     */
    private UserType senderType;

    /**
     * 消息接受者类型
     */
    private UserType receiverType;
}

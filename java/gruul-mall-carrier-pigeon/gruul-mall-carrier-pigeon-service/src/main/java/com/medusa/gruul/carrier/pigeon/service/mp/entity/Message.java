package com.medusa.gruul.carrier.pigeon.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.carrier.pigeon.api.enums.MessageType;
import com.medusa.gruul.carrier.pigeon.api.enums.UserType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 店铺用户聊天消息
 * </p>
 *
 * @author 张治保
 * @since 2022-10-10
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_message",excludeProperty = "deleted")
public class Message extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 店铺客服用户id
     */
    private Long adminId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 消息发送者类型
     */
    private UserType senderType;

    /**
     * 消息接受者类型
     */
    private UserType receiverType;

    /**
     * 消息媒体类型
     */
    private MessageType messageType;

    /**
     * 内容
     */
    private String message;

    /**
     * 是否已读
     */
    @TableField("`read`")
    private Boolean read;

    /**
     * 管理员昵称
     */
    @TableField(exist = false)
    private String nickname;

}

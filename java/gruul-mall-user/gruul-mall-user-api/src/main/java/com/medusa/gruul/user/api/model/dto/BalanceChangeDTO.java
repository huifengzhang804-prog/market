package com.medusa.gruul.user.api.model.dto;

import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.model.message.DataChangeMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 余额变化DTO 商家充值 个人充值 系统赠送
 *
 * @author xiaoq
 * @Description
 * @date 2022-12-21 17:37
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class BalanceChangeDTO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 会员昵称
     */
    private String userNikeName;

    /**
     * 用户头像
     */
    private String userHeadPortrait;

    /**
     * 支付枚举
     */
    private PayType payType;
    /**
     * 个人数据变化消息
     */
    private DataChangeMessage personDataChangeMessage;

    /**
     * 系统数据变化消息
     */
    private DataChangeMessage systemDataChangeMessage;
}

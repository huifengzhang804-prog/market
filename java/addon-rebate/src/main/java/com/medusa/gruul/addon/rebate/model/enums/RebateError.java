package com.medusa.gruul.addon.rebate.model.enums;

import com.medusa.gruul.global.i18n.I18N;
import com.medusa.gruul.global.model.exception.Error;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author jinbu
 */

@Getter
@RequiredArgsConstructor
public enum RebateError implements Error {

    /**
     * 消费返利未开启
     */
    CONSUMPTION_REBATE_NOT_OPENED(57006, "consumption.rebate.not.opened"),
    /**
     * 提现账号不存在
     */
    WITHDRAW_ACCOUNT_NOT_EXISTED(57007, "withdraw.account.not.existed"),

    /**
     * 微信未绑定
     */
    WECHAT_NOT_BOUND(57008, "WeChat.not.bound"),

    /**
     * 返利信息不存在
     */
    REBATE_INFORMATION_DOES_NOT_EXIST(57009, "rebate.information.does.not.exist"),

    /**
     * 提现金额未达到最低门槛
     */
    WITHDRAW_AMOUNT_NOT_REACH_MINIMUM_THRESHOLD(57010, "withdraw.amount.not.reach.minimum.threshold"),

    /**
     * 返利余额不足
     */
    REBATE_BALANCE_INSUFFICIENT(57011, "rebate.balance.insufficient"),

    /**
     * 消费返利已停用
     */
    REBATE_DISABLED(57012, "rebate.disabled"),
    ;


    private final int code;

    private final String msgCode;

    @Override
    public int code() {
        return getCode();
    }

    @Override
    public String msg() {
        return I18N.msg(getMsgCode());
    }
}

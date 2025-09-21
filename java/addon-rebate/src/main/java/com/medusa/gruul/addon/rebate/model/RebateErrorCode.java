package com.medusa.gruul.addon.rebate.model;

public interface RebateErrorCode {


    /**
     * 消费返利设置不存在
     */
    int REBATE_CONFIG_NOT_EXISTS = 57001;

    /**
     * 会员等级不存在
     */
    int MEMBER_RANK_CODE_NOT_EXISTS = 57002;

    /**
     * 提现账号不存在
     */
    int WITHDRAW_ACCOUNT_NOT_EXISTED = 57007;

}

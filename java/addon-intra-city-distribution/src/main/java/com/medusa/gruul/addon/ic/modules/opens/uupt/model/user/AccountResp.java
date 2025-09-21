package com.medusa.gruul.addon.ic.modules.opens.uupt.model.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * @since 2024/8/12
 */
@Getter
@Setter
@ToString
public class AccountResp {
    /**
     * 账户余额（分）
     */
    private Long balance;

    /**
     * 冻结金额（分）
     */
    private Long frozenAmount;
}

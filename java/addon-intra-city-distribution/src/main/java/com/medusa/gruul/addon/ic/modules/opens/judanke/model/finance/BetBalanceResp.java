package com.medusa.gruul.addon.ic.modules.opens.judanke.model.finance;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/7
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class BetBalanceResp {
    /**
     * total_avail	int	是
     * 可消费金额+冻结金额=总可消费金额（单位：分）
     */
    private Long totalAvail;

    /**
     * avail	int	是
     * 可消费账户余额（单位：分）
     */
    private Long avail;

    /**
     * frozen	int	是
     * 冻结账户余额（单位：分）
     */
    private Long frozen;

}

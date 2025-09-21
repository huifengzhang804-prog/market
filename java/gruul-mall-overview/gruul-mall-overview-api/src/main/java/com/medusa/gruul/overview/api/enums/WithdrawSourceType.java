package com.medusa.gruul.overview.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 提现工单 来源类型
 *
 * @author 张治保
 * date 2022/11/19
 */

@Getter
@RequiredArgsConstructor
public enum WithdrawSourceType {

    /**
     * 分销佣金
     */
    DISTRIBUTE(1),

    /**
     * 店铺佣金
     */
    SHOP(2),

    /**
     * 消费返利
     */
    CONSUMPTION_REBATE(3),
    ;

    @EnumValue
    private final Integer value;

    public String getDesc() {
        switch (this) {
            case DISTRIBUTE:
                return "分销佣金提现";
            case SHOP:
                return "店铺分佣提现";
            case CONSUMPTION_REBATE:
                return "消费返利提现";
            default:
                return "其它提现";
        }
    }
}

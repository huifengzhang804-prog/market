package com.medusa.gruul.addon.ic.modules.opens.uupt.model.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * @since 2024/8/12
 */
@Getter
@Setter
@ToString(callSuper = true)
public class CancelFeeResp extends OrderKey {

    /**
     * 取消费用（单位：分）
     */
    private Long cancelFee;
}

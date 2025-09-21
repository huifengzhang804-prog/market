package com.medusa.gruul.addon.ic.modules.opens.judanke.model.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * @since 2024/8/8
 */
@Getter
@Setter
@ToString
public class PreCancelResp {
    /**
     * deduct_fee	int	是
     * 取消订单违约金（单位：分）
     */
    private Long deductFee;
}

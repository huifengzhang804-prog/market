package com.medusa.gruul.addon.rebate.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2023/9/15
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class RebatePayDTO implements Serializable {

    /**
     * 该比订单是否不能使用返利支付
     */
    private Boolean disabled;

    /**
     * 返利支付金额
     */
    private Long balance;
}

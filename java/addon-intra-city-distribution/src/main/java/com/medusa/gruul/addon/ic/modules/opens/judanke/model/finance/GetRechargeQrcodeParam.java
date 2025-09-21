package com.medusa.gruul.addon.ic.modules.opens.judanke.model.finance;

import com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums.PayMethod;
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
public class GetRechargeQrcodeParam {

    /**
     * money	int	是
     * 金额（单位分，最小1元，最大8000元）
     */
    private Long money;

    /**
     * pay_method	string	是
     * 二维码类型（wxsan=微信二维码，alisan=支付宝二维码）
     */
    private PayMethod payMethod;
}

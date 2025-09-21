package com.medusa.gruul.addon.ic.modules.opens.judanke.model.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/8
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class AddTipsParam {
    /**
     * relative_user_id	string	是
     * 聚单客商家ID
     */
    private Long relativeUserId;

    /**
     * order_id	int	是
     * 订单id
     */
    private Long orderId;

    /**
     * tip_fee	int	是
     * 小费金额（单位：分）
     */
    private Long tipFee;
}

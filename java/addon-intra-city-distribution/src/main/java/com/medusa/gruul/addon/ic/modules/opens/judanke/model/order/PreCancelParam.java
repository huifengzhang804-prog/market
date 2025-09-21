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
public class PreCancelParam {
    /**
     * order_id	int	是
     * 订单id
     */
    private Long orderId;

    /**
     * relative_user_id	int	是
     * 聚单客商家ID
     */
    private Long relativeUserId;

    /**
     * cancel_reason	string	是
     * 取消原因
     */
    private String cancelReason;
}

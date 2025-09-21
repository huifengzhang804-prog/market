package com.medusa.gruul.addon.ic.modules.opens.judanke.model.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/6
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderCreateResp {
    /**
     * order_id	int	是 订单id
     */
    private Long orderId;
}

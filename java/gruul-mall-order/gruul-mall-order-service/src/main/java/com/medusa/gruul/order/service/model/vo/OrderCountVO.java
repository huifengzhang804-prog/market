package com.medusa.gruul.order.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * @since 2024/7/9
 */
@Getter
@Setter
@ToString
public class OrderCountVO {

    /**
     * 待支付
     */
    private Long unpaid = 0L;

    /**
     * 待发货
     */
    private Long unDelivery = 0L;

    /**
     * 小程序代发货
     */
    private Long mpUnDelivery = 0L;

    /**
     * 待收货
     */
    private Long unReceive = 0L;
}

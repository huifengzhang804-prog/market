package com.medusa.gruul.order.api.model.ic;

import com.medusa.gruul.order.api.enums.ICShopOrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2024/11/28
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ICStatus implements Serializable {
    /**
     * 订单状态
     */
    private ICShopOrderStatus status;

    /**
     * 订单状态描述
     */
    private String statusDesc;
}

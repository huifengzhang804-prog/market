package com.medusa.gruul.order.service.modules.deliver.model;

import com.medusa.gruul.order.service.model.bo.DeliveryQueryBO;
import com.medusa.gruul.order.service.model.dto.OrderDeliveryDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/11/2
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DeliveryParam {
    private DeliveryQueryBO deliveryMatch;
    private OrderDeliveryDTO orderDelivery;
}

package com.medusa.gruul.order.service.model.vo;

import com.medusa.gruul.order.api.entity.OrderReceiver;
import com.medusa.gruul.order.api.entity.ShopOrderPackage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 *
 *
 * @author 张治保
 * date 2022/8/13
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DeliveredPackageFirstVO {

    private OrderReceiver orderReceiver;

    private ShopOrderPackage shopOrderPackage;

}

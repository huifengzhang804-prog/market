package com.medusa.gruul.addon.ic.modules.opens.judanke.model.order;

import com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums.DeliveryType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeliveryExtend {

    /**
     * 运力类型（0=聚合运力，1=绑定运力）
     */
    private DeliveryType deliveryType;
}
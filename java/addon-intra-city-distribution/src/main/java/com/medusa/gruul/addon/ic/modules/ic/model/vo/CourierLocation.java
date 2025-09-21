package com.medusa.gruul.addon.ic.modules.ic.model.vo;

import com.medusa.gruul.addon.ic.modules.ic.model.bo.ICCourier;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/29
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class CourierLocation {

    private ICCourier courier;


}

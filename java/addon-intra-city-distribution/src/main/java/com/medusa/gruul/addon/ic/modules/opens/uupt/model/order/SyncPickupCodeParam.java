package com.medusa.gruul.addon.ic.modules.opens.uupt.model.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/13
 */
@Getter
@Setter
@ToString(callSuper = true)
@Accessors(chain = true)
public class SyncPickupCodeParam extends OrderKey {

    /**
     * 取件码
     */
    private String pickupCode;
}

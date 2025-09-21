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
public class AddGratuityParam extends OrderKey {

    /**
     * 支付小费金额（单位元）
     */
    private Long fee;
}

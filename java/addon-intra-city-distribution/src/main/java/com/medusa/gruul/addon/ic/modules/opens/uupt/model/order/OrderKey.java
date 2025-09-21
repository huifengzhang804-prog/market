package com.medusa.gruul.addon.ic.modules.opens.uupt.model.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/12
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderKey {
    /**
     * 第三方对接平台订单id  与orderCode任选其一即可
     */
    private String originId;

    /**
     * UU跑腿订单编号 与originId任选其一即可
     */
    private String orderCode;
}

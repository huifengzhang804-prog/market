package com.medusa.gruul.addon.ic.modules.opens.uupt.model.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * @since 2024/8/12
 */
@Getter
@Setter
@ToString
public class OrderPriceDetailResp {
    /**
     * 费用
     */
    private Long fee;

    /**
     * 费用项名
     */
    private String name;
}

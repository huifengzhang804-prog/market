package com.medusa.gruul.addon.ic.modules.opens.uupt.model.order;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/12
 */
@Getter
@Setter
@Accessors(chain = true)
public class ProductDetail {
    /**
     * 否 货物名称
     */
    private String goodsName;

    /**
     * 否 货物数量
     */
    private Integer num;
}

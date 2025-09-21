package com.medusa.gruul.addon.ic.modules.ic.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/9/2
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DeliverItemPriceVO {

    private Long total;

    private Long discount;

    private Long pay;

}

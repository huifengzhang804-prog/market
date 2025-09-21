package com.medusa.gruul.cart.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * date 2022/5/16
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductSkuInvalidVO extends ProductSkuVO{
    /**
     * 不可用原因
     */
    private Integer reason;
}

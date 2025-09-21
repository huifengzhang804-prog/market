package com.medusa.gruul.goods.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/6/26
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductPriceItemVO {

    /**
     * 价格
     */
    private Long price;

    /**
     * 预估价
     */
    private String desc;
}

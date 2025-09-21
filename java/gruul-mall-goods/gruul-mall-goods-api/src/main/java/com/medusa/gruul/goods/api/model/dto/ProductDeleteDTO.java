package com.medusa.gruul.goods.api.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * @author 张治保
 * date 2022/11/15
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductDeleteDTO {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 商品id集合
     */
    private Set<Long> productIds;
}

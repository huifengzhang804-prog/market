package com.medusa.gruul.goods.api.model.dto;

import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * @author 张治保
 * date 2022/11/15
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductUpdateStatusDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 商品id集合
     */
    private Set<Long> productIds;

    /**
     * 目标商品状态
     */
    private ProductStatus productStatus;
}

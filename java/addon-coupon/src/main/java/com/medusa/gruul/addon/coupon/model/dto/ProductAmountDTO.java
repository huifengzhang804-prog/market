package com.medusa.gruul.addon.coupon.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * date 2022/11/5
 */
@Getter
@Setter
@ToString
public class ProductAmountDTO {

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 该商品总金额
     */
    private Long amount;

}

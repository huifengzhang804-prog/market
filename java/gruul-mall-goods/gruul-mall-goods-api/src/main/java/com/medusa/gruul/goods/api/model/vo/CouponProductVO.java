package com.medusa.gruul.goods.api.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 优惠券关联商品VO
 *
 * @author wufuzhong
 * @Date 2023-12-07
 */
@Data
@Accessors(chain = true)
public class CouponProductVO implements Serializable {

    private static final long serialVersionUID = 1565552066349146739L;

    /**
     * 商品id
     */
    private Long productId;
}

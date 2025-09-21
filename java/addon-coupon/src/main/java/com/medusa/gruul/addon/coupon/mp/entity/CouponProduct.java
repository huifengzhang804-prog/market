package com.medusa.gruul.addon.coupon.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 优惠券商品关联表
 *
 * @author 张治保
 * @since 2022-11-02
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_coupon_product")
public class CouponProduct extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 优惠券id
     */
    private Long couponId;


}

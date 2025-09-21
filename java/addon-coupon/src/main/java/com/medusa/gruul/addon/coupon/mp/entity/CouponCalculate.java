package com.medusa.gruul.addon.coupon.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 *
 * @author 张治保
 * @since 2022-11-07
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_coupon_calculate")
public class CouponCalculate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务唯一id
     */
    private String bid;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 商品总金额
     */
    private Long amount;


}

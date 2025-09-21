package com.medusa.gruul.addon.coupon.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.handler.type.MapToKeyValuesTypeHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * @author 张治保
 * @since 2022-11-11
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_coupon_order_record", autoResultMap = true)
public class CouponOrderRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 买家id
     */
    private Long buyerId;

    /**
     * 店铺id 与优惠券id对应的map
     */
    @TableField(typeHandler = MapToKeyValuesTypeHandler.class)
    private Map<Long, Long> coupons;


}

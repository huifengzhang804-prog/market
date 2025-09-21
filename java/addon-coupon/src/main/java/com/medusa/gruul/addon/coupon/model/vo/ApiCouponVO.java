package com.medusa.gruul.addon.coupon.model.vo;

import com.medusa.gruul.addon.coupon.model.enums.CouponProductType;
import com.medusa.gruul.addon.coupon.model.enums.CouponType;
import com.medusa.gruul.addon.coupon.model.enums.EffectType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author 张治保
 * @since 2022/11/3
 */
@Getter
@Setter
@Accessors(chain = true)
public class ApiCouponVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户与优惠券对应关系id
     */
    private Long couponUserId;

    /**
     * 店铺id 平台优惠券为0
     */
    private Long shopId;

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 优惠券类型
     */
    private CouponType type;

    /**
     * 优惠券生效类型
     */
    private EffectType effectType;

    /**
     * 限领券几日内可用
     */
    private Integer days;

    /**
     * 固定时间生效开始时间
     */
    private LocalDate startDate;

    /**
     * 固定时间生效结束时间
     */
    private LocalDate endDate;

    /**
     * 满减/满折 需要满足的金额
     */
    private Long requiredAmount;

    /**
     * 同一个店铺 按照 无门槛现金券、无门槛折扣券、满减券、满折券 排序
     */
    private Integer rowNum;

    /**
     * 计算出的 优惠金额 目前仅在 结算页使用优惠券
     */
    private Long discountAmount;

    /**
     * 优惠金额
     */
    private Long amount;

    /**
     * 折扣比 1
     */
    private BigDecimal discount;

    /**
     * 作用的商品类型
     */
    private CouponProductType productType;

}

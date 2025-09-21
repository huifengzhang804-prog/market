package com.medusa.gruul.addon.coupon.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.medusa.gruul.addon.coupon.model.enums.CouponProductType;
import com.medusa.gruul.addon.coupon.model.enums.CouponType;
import com.medusa.gruul.addon.coupon.model.enums.EffectType;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * @author 张治保
 * date 2022/11/2
 */
@Getter
@Setter
@Accessors(chain = true)
public class BaseCouponModel extends BaseEntity {

    private static final long serialVersionUID = 1L;

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
     * 满减/满折 需要满足的金额
     */
    private Long requiredAmount;

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

    /**
     * 固定时间生效开始时间
     */
    private LocalDate startDate;

    /**
     * 固定时间生效结束时间
     */
    private LocalDate endDate;

    /**
     * 面值(优惠金额amount 或者 折扣比discount)
     */
    @TableField(exist = false)
    private String parValue;


    /**
     * 优惠金额
     * 折扣比
     */
    public String parValue() {
        if (null != amount) {
            return BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(CommonPool.UNIT_CONVERSION_TEN_THOUSAND), CommonPool.NUMBER_TWO, RoundingMode.HALF_UP).toPlainString();
        } else if (null != discount) {
            return discount.stripTrailingZeros().toPlainString() + "折";
        }
        return null;
    }

}

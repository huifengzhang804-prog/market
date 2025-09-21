package com.medusa.gruul.addon.coupon.model.dto;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.addon.coupon.model.enums.CouponStatus;
import com.medusa.gruul.addon.coupon.model.enums.CouponType;
import com.medusa.gruul.addon.coupon.model.enums.EffectType;
import com.medusa.gruul.addon.coupon.mp.entity.Coupon;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.web.valid.annotation.Price;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.o.BaseDTO;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author 张治保
 * date 2022/11/2
 */
@Getter
@Setter
@ToString
public class CouponDTO extends CouponWorkingEditDTO implements BaseDTO {


    /**
     * 优惠券类型
     */
    @NotNull
    private CouponType type;

    /**
     * 满减满折需要的满足的金额
     */
    @Price
    private Long requiredAmount;

    /**
     * 折扣力度
     */
    @DecimalMin("0.1")
    @DecimalMax("9.9")
    @Digits(integer = 1, fraction = 1)
    private BigDecimal discount;

    /**
     * 优惠金额 现金券金额
     */
    @Price
    private Long amount;


    /**
     * 优惠券生效类型
     */
    @NotNull
    private EffectType effectType;

    /**
     * 固定日期段 开始日期
     */
    private LocalDate startDate;

    /**
     * 优惠券发行数量
     */
    @NotNull
    @Min(1)
    private Long num;

    /**
     * 每人限领
     */
    @NotNull
    @Min(1)
    private Integer limit;


    @Override
    public void validParam(boolean isPlatform) {
        if (isPlatform && !(NumberUtil.equals(getNum(), Long.valueOf(getLimit())))) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR);
        }
        this.checkCouponTypeParams();
        this.checkEffectTypeParams();
        super.validParam(isPlatform);
    }


    /**
     * 检查优惠券类型及其 额外参数
     */
    private void checkCouponTypeParams() {
        CouponType couponType = this.getType();

        //检查是否有金额限制
        Boolean amountLimit = couponType.getRequiredAmount();
        if (!amountLimit) {
            this.setRequiredAmount(null);
        }
        if (amountLimit && getRequiredAmount() == null) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR);
        }
        //检查是折扣优惠还是现金优惠
        if (couponType.getHasDiscount()) {
            if (getDiscount() == null) {
                throw new GlobalException(SystemCode.PARAM_VALID_ERROR);
            }
            this.setAmount(null);
            return;
        }
        if (getAmount() == null) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR);
        }
        this.setDiscount(null);
    }

    /**
     * 检查优惠券生效类型 及其 额外参数
     */
    private void checkEffectTypeParams() {
        EffectType effectType = getEffectType();
        //检查立即生效的天数
        if (!effectType.getIsPeriod()) {
            if (this.getDays() == null) {
                throw new GlobalException(SystemCode.PARAM_VALID_ERROR);
            }
            this.setStartDate(null);
            this.setEndDate(null);
            return;
        }
        //检查日期期间是否设置正确
        LocalDate start = this.getStartDate();
        LocalDate end = this.getEndDate();
        if (start == null || end == null) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR);
        }
        //开始日期不能小于当前日期
        if (start.isBefore(LocalDate.now())) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR);
        }
        //结束日期 不能小于开始日期
        if (end.isBefore(start)) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR);
        }
        this.setDays(null);
    }

    public Coupon toCouponEntity(Long shopId) {
        Coupon couponEntity = new Coupon()
//                .setPlatformDeleteFlag(Boolean.FALSE)
//                .setShopDeleteFlag(Boolean.FALSE)
                .setStatus(CouponStatus.OK)
                .setDays(getDays())
                .setNum(getNum())
                .setStock(getNum())
                .setLimit(getLimit());

        couponEntity.setShopId(shopId)
                .setType(getType())
                .setName(StrUtil.trim(getName()))
                .setEffectType(getEffectType())
                .setStartDate(getStartDate())
                .setEndDate(getEndDate())
                .setRequiredAmount(getRequiredAmount())
                .setAmount(getAmount())
                .setDiscount(getDiscount())
                .setProductType(getProductType());
        return couponEntity;
    }

}

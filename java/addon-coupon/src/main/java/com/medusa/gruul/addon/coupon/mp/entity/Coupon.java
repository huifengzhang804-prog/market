package com.medusa.gruul.addon.coupon.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.addon.coupon.model.BaseCouponModel;
import com.medusa.gruul.addon.coupon.model.CouponErrorCode;
import com.medusa.gruul.addon.coupon.model.dto.CouponUserInfoDTO;
import com.medusa.gruul.addon.coupon.model.enums.CouponProductType;
import com.medusa.gruul.addon.coupon.model.enums.CouponStatus;
import com.medusa.gruul.addon.coupon.model.enums.EffectType;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.global.model.exception.GlobalException;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * 优惠券
 *
 * @author 张治保
 * @since 2022-11-02
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_coupon")
public class Coupon extends BaseCouponModel {

    /**
     * 优惠券状态
     */
    private CouponStatus status;

    /**
     * 立即生效天数
     */
    private Integer days;

    /**
     * 发行数量
     */
    private Long num;

    /**
     * 剩余库存
     */
    private Long stock;

    /**
     * 每人限领
     */
    @TableField("`limit`")
    private Integer limit;

    /**
     * 违规下架原因
     */
    @TableField("violation_reason")
    private String violationReason;

    /**
     * 已使用的数量
     */
    @TableField(exist = false)
    private Long usedCount;

    /**
     * 店铺名称
     */
    @TableField(exist = false)
    private String shopName;

    /**
     * 商品id列表
     */
    @TableField(exist = false)
    private Set<Long> productIds;

    /**
     * 优惠券状态文案
     */
    @TableField(exist = false)
    private String statusText;

    /**
     * 优惠券的使用描述
     * 无门槛10元
     * 无门槛打8折
     * 满30减9
     * 满30打8折
     */
    @TableField(exist = false)
    private String consumerDesc;

    public Tuple2<LocalDate, LocalDate> calcStartAnEndDate() {
        LocalDate now = LocalDate.now();
        LocalDate endDate = getEndDate();
        LocalDate startDate = getStartDate();
        return switch (getEffectType()) {
            case IMMEDIATELY -> Tuple.of(now, now.plusDays(getDays() - 1));
            case PERIOD -> {
                if (endDate.isBefore(now)) {
                    throw new GlobalException(CouponErrorCode.COUPON_INVALID, "优惠券已失效");
                }
                yield Tuple.of(startDate.isAfter(now) ? startDate : now, endDate);
            }
        };
    }

    public CouponUser newCouponUser(CouponUserInfoDTO userBaseInfo, BiFunction<Long, Long, Set<Long>> productIdsFunction) {
        CouponProductType productType = getProductType();
        Long shopId = getShopId();
        Long couponId = getId();
        CouponUser couponUser = new CouponUser()
                .setUserId(userBaseInfo.getCouponUserInfo().getUserId())
                .setUserPhone(userBaseInfo.getCouponUserInfo().getMobile())
                .setCollectType(userBaseInfo.getCollectType())
                .setCouponId(couponId)
                .setUsed(Boolean.FALSE)
                .setProductIds(productType.getIsAssigned() ? productIdsFunction.apply(shopId, couponId) : null);
        couponUser.setShopId(shopId)
                .setName(getName())
                .setType(getType())
                .setEffectType(getEffectType())
                .setRequiredAmount(getRequiredAmount())
                .setAmount(getAmount())
                .setDiscount(getDiscount())
                .setProductType(productType);
        Tuple2<LocalDate, LocalDate> startAnEndDate = calcStartAnEndDate();
        couponUser.setStartDate(startAnEndDate._1())
                .setEndDate(startAnEndDate._2());
        // 领券或赠券时, 初始赋值为当前店铺; 如果为平台券, 在用券时还会更新增加用券店铺集合
        couponUser.setAssociatedShopIds(Set.of(shopId));

        return couponUser;
    }


    public CouponUser newCouponUser(CouponUserInfoDTO userBaseInfo, Tuple2<LocalDate, LocalDate> startAnEndDate, Set<Long> productIds) {
        CouponProductType productType = getProductType();
        Long shopId = getShopId();
        Long couponId = getId();
        CouponUser couponUser = new CouponUser()
                .setUserId(userBaseInfo.getCouponUserInfo().getUserId())
                .setUserPhone(userBaseInfo.getCouponUserInfo().getMobile())
                .setGiftUserId(userBaseInfo.getGiftUserInfo().getUserId())
                .setGiftUserPhone(userBaseInfo.getGiftUserInfo().getMobile())
                .setCollectType(userBaseInfo.getCollectType())
                .setCouponId(couponId)
                .setUsed(Boolean.FALSE)
                .setProductIds(productType.getIsAssigned() ? productIds : null);
        couponUser.setShopId(shopId)
                .setName(getName())
                .setType(getType())
                .setEffectType(getEffectType())
                .setRequiredAmount(getRequiredAmount())
                .setAmount(getAmount())
                .setDiscount(getDiscount())
                .setProductType(productType);
        couponUser.setStartDate(startAnEndDate._1())
                .setEndDate(startAnEndDate._2());
        // 领券或赠券时, 初始赋值为当前店铺; 如果为平台券, 在用券时还会更新增加用券店铺集合
        couponUser.setAssociatedShopIds(Set.of(shopId));

        return couponUser;
    }


    /**
     * 是否处于 未开始 的状态
     *
     * @return 只有未开始的为TRUE 其余都为FALSE
     */
    public boolean notStated() {

        if (CouponStatus.OK != status) {
            return false;
        }
        //需要是正常状态
        //只有 固定 日期期间 类型的优惠券 才有未开始的状态
        return EffectType.PERIOD == getEffectType() &&
                LocalDate.now().isBefore(getStartDate());
    }

    /**
     * 是否处于 进行中 状态
     *
     * @return 进行中的为TRUE 其余为FALSE
     */
    public boolean running() {
        if (CouponStatus.OK != status) {
            return false;
        }
        EffectType effectType = getEffectType();
        if (EffectType.IMMEDIATELY == effectType) {
            // 立刻生效的优惠券类型 当存在库存的时候 是运行中的
            return stock > CommonPool.NUMBER_ZERO;
        }
        //时间段的优惠券 只有在开始与结束时间段之中 且库存大于0 的时候 是运行中的
        //这里日期开始加1天 结束减一天 是因为如果是相同的日期
        return LocalDate.now().isAfter(getStartDate().minusDays(1)) &&
                LocalDate.now().isBefore(getEndDate().plusDays(1))
                && stock > CommonPool.NUMBER_ZERO;
    }

    /**
     * 是否处于已结束的状态
     *
     * @return 已结束 的为TRUE 其余为FALSE
     */
    public boolean finished() {
        if (CouponStatus.OK != status) {
            return false;
        }
        //正常状态下
        if (stock <= CommonPool.NUMBER_ZERO) {
            //不存在库存的 则是已结束的
            return true;
        }
        //固定 日期期间 类型的优惠券 结束时间到了 则是已结束的
        return EffectType.PERIOD == getEffectType() && LocalDate.now().isAfter(getEndDate());
    }

    /**
     * 是否是平台违规下架
     *
     * @return 违规下架的为TRUE 其余为FALSE
     */
    public boolean platformIllegalOffShelf() {
        return CouponStatus.BANED == status;
    }

    /**
     * 是否是店铺下架
     *
     * @return 店铺下架的为TRUE 其余为FALSE
     */
    public boolean shopOffShelf() {
        return CouponStatus.SHOP_BANED == status;
    }


}

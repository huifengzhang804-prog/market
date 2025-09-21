package com.medusa.gruul.addon.coupon.model.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.medusa.gruul.global.model.helper.AmountCalculateHelper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

/**
 * 优惠券类型
 *
 * @author 张治保
 * date 2022/11/2
 */
@Getter
@RequiredArgsConstructor
public enum CouponType {

    /**
     * 无门槛现金券
     */
    PRICE_REDUCE(1, Boolean.FALSE, Boolean.FALSE, "无门槛现金券"),

    /**
     * 无门槛折扣券
     */
    PRICE_DISCOUNT(2, Boolean.FALSE, Boolean.TRUE, "无门槛折扣券"),
    /**
     * 满减券
     */
    REQUIRED_PRICE_REDUCE(3, Boolean.TRUE, Boolean.FALSE, "满减券"),

    /**
     * 满折券
     */
    REQUIRED_PRICE_DISCOUNT(4, Boolean.TRUE, Boolean.TRUE, "满折券");


    private final static String REQUIRED_AMOUNT_TEMPLATE = "满{}元";
    private final static String NOT_REQUIRED_TEMPLATE = "无门槛";
    private final static String DISCOUNT_TEMPLATE = "{}折";
    private final static String NOT_DISCOUNT_TEMPLATE = "减{}元";
    private final static String CONSUMER_PRODUCT_REQUIRED_AMOUNT_TEMPLATE = "满{}";
    private final static String CONSUMER_PRODUCT_NOT_REQUIRED_TEMPLATE = "无门槛{}{}";
    private final static String CONSUMER_PRODUCT_DISCOUNT_TEMPLATE = "打{}折";
    private final static String CONSUMER_PRODUCT_NOT_DISCOUNT_TEMPLATE = "减{}";
    private final static String AMOUNT_UNIT = "元";
    private final static String SEPARATOR = ",";
    private final static String USE_RECORD_NOT_REQUIRED_TEMPLATE = "无门槛使用";
    private final static String USE_RECORD_NOT_DISCOUNT_TEMPLATE = "{}元";
    @EnumValue
    private final Integer value;
    /**
     * 是否有金额限制 （满足 多少 金额 优惠券才生效）
     */
    private final Boolean requiredAmount;
    /**
     * 是否是折扣优惠 true 是  false 现金优惠
     */
    private final Boolean hasDiscount;

    private final String text;


    /**
     * 获取优惠的价格
     *
     * @param totalAmount 目前的商品总价
     * @param amount      优惠金额
     * @param discount    折扣
     * @return 优惠价
     */
    public Long getDiscountAmount(Long totalAmount, Long amount, BigDecimal discount) {
        if (getHasDiscount()) {
            return AmountCalculateHelper.getDiscountAmountByDiscount(totalAmount, discount);
        }
        return AmountCalculateHelper.getDiscountAmountByAmount(totalAmount, amount);
    }

    public String getDesc(Long requiredAmount, Long amount, BigDecimal discount) {
        return (getRequiredAmount() ? StrUtil.format(REQUIRED_AMOUNT_TEMPLATE, requiredAmount / 10000) : NOT_REQUIRED_TEMPLATE) +
                (getHasDiscount() ? StrUtil.format(DISCOUNT_TEMPLATE, discount.toPlainString()) : StrUtil.format(NOT_DISCOUNT_TEMPLATE, amount / 10000));
    }

    /**
     * C端商品列表 优惠券描述
     * 无门槛10元
     * 无门槛打8折
     * 满30减9
     * 满30打8折
     */
    public String getConsumerDesc(Long requiredAmount, Long amount, BigDecimal discount) {
        if (!getRequiredAmount() && !getHasDiscount()) {
            return StrUtil.format(CONSUMER_PRODUCT_NOT_REQUIRED_TEMPLATE, amount / 10000, AMOUNT_UNIT);
        }

        return (getRequiredAmount() ? StrUtil.format(CONSUMER_PRODUCT_REQUIRED_AMOUNT_TEMPLATE, requiredAmount / 10000)
                : NOT_REQUIRED_TEMPLATE)
                .concat(
                        (getHasDiscount() ? StrUtil.format(CONSUMER_PRODUCT_DISCOUNT_TEMPLATE, discount.stripTrailingZeros().toPlainString())
                                : StrUtil.format(CONSUMER_PRODUCT_NOT_DISCOUNT_TEMPLATE, amount / 10000))
                );
    }


    /**
     * 平台端或店铺端-用券记录, 优惠券描述
     * <p>
     * 10元,无门槛使用
     * 1.5折,无门槛使用
     * 满200元,减10元
     * 满200元,打1.5折
     */
    public String getUserRecordDesc(Long requiredAmount, Long amount, BigDecimal discount) {
        // 无门槛使用时
        if (!getRequiredAmount()) {
            return (getHasDiscount() ? StrUtil.format(DISCOUNT_TEMPLATE, discount.stripTrailingZeros().toPlainString())
                    : StrUtil.format(USE_RECORD_NOT_DISCOUNT_TEMPLATE, amount / 10000, AMOUNT_UNIT))
                    .concat(SEPARATOR)
                    .concat(USE_RECORD_NOT_REQUIRED_TEMPLATE);
        }

        return StrUtil.format(REQUIRED_AMOUNT_TEMPLATE, requiredAmount / 10000)
                .concat(SEPARATOR)
                .concat(
                        (getHasDiscount() ? StrUtil.format(CONSUMER_PRODUCT_DISCOUNT_TEMPLATE, discount.stripTrailingZeros().toPlainString())
                                : StrUtil.format(NOT_DISCOUNT_TEMPLATE, amount / 10000))
                );
    }


}

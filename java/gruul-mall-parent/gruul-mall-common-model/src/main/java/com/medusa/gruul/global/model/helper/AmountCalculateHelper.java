package com.medusa.gruul.global.model.helper;

import cn.hutool.core.lang.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 金额计算辅助类
 *
 * @author 张治保
 * date 2022/11/9
 */
public interface AmountCalculateHelper {

    long BASE_MULTIPLE = 10000;
    long BASE_PRICE_FORMATTER = 100;

    BigDecimal BASE_MULTIPLE_DECIMAL = BigDecimal.valueOf(BASE_MULTIPLE);

    int BASE_SCALE = 2;

    int MILLI_SCALE = -2;
    int DEFAULT_SCALE = 10;

    /**
     * 价格格式化
     * egs.
     * 1101 -> 1100 | 1200
     * 1110 -> 1100 | 1200
     * 1100 -> 1100 | 1100
     *
     * @param down     是否向下取整
     * @param oriPrice 原始价格
     * @return 格式化后的价格
     */
    static long priceFormat(boolean down, long oriPrice) {
        //整数未
        long ints = (oriPrice / BASE_PRICE_FORMATTER) * BASE_PRICE_FORMATTER;
        if (down) {
            return ints;
        }
        //余数
        long remains = oriPrice % BASE_PRICE_FORMATTER;
        //如果余数大于0向上取整
        if (remains == 0) {
            return ints;
        }
        //如果余数大于0向上取整
        if (remains > 0) {
            return ints + BASE_PRICE_FORMATTER;
        }
        //如果余数小于0向下取整
        return ints - BASE_PRICE_FORMATTER;
    }


    /**
     * 价格格式化
     *
     * @param down     是否向下取整
     * @param oriPrice 原始价格
     * @return 格式化后的价格
     */
    static long priceFormat(boolean down, double oriPrice) {
        //直接截断小数位
        return priceFormat(down, (long) oriPrice);
    }

    /**
     * 元 转为 毫
     *
     * @param yuanPrice 单位元的价格
     * @return 单位为毫的价格
     */
    static Long toMilli(BigDecimal yuanPrice) {
        Assert.notNull(yuanPrice);
        return priceFormat(true, yuanPrice.doubleValue() * BASE_MULTIPLE);
    }


    /**
     * 元 转为 毫
     *
     * @param milliPrice 单位毫的价格
     * @return 单位为元的价格
     */
    static BigDecimal toYuan(Long milliPrice) {
        Assert.notNull(milliPrice);
        return BigDecimal.valueOf(milliPrice).divide(BASE_MULTIPLE_DECIMAL, BASE_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 获取实际成交价 = 销售价 - 销售价*(折扣/总价)
     *
     * @param salePrice      销售价
     * @param discountAmount 折扣价
     * @param totalAmount    折扣钱的总价
     * @return 实际成交价
     */
    static Long getDealAmount(Long salePrice, Long discountAmount, Long totalAmount) {
        Assert.notNull(salePrice);
        return salePrice - getDiscountAmount(salePrice, discountAmount, totalAmount);

    }

    /**
     * 计算实际成交价 = 销售价 - 所有优惠项的优惠价
     *
     * @param salePrice       销售价
     * @param discountAmounts 所有优惠价
     * @return 实际成交价
     */
    static Long getDealAmount(Long salePrice, Long... discountAmounts) {
        Assert.notNull(salePrice);
        long result = salePrice;
        for (Long current : discountAmounts) {
            Assert.notNull(current);
            result = result - ((current == null || current < 0) ? 0 : current);
            if (result <= 0) {
                return 0L;
            }
        }
        return result;
    }

    /**
     * 获取实际优惠价 = 销售价 *（总折扣/总价）
     *
     * @param salePrice      销售价
     * @param discountAmount 折扣价
     * @param totalAmount    折扣钱的总价
     * @return 实际优惠价
     */
    static Long getDiscountAmount(Long salePrice, Long discountAmount, Long totalAmount) {
        return AmountCalculateHelper.getDiscountAmount(
                salePrice,
                AmountCalculateHelper.getDiscountRate(discountAmount, totalAmount)
        );
    }

    /**
     * 获取实际优惠价 = 成交价 * 优惠比率
     *
     * @param totalAmount  商品总额
     * @param discountRate 折扣比率
     * @return 实际优惠价
     */
    static Long getDiscountAmount(Long totalAmount, BigDecimal discountRate) {
        Assert.notNull(totalAmount);
        Assert.notNull(discountRate);
        return BigDecimal.valueOf(totalAmount)
                .multiply(discountRate)
                .setScale(MILLI_SCALE, RoundingMode.DOWN)
                .longValue();
    }

    /**
     * 根据 总价 与打折额度 情况 获取折扣价  = 总价 x((10-折扣额度)/10)
     *
     * @param totalAmount 总价
     * @param discount    折扣额度 比如 9。5折
     * @return 折扣价
     */
    static Long getDiscountAmountByDiscount(Long totalAmount, BigDecimal discount) {
        Assert.notNull(totalAmount);
        Assert.notNull(discount);
        BigDecimal totalAmountDecimal = BigDecimal.valueOf(totalAmount);
        return totalAmountDecimal.multiply(
                        (BigDecimal.TEN.subtract(discount))
                                .divide(BigDecimal.TEN, AmountCalculateHelper.DEFAULT_SCALE, RoundingMode.DOWN)
                ).setScale(AmountCalculateHelper.MILLI_SCALE, RoundingMode.DOWN)
                .longValue();
    }

    /**
     * 根据 总价 与 现金券额度 获取折扣价 = 总价>= 现金额度？现金额度：总价
     *
     * @param totalAmount 总价
     * @param amount      现金额度
     * @return 折扣价
     */
    static Long getDiscountAmountByAmount(Long totalAmount, Long amount) {
        Assert.notNull(totalAmount);
        Assert.notNull(amount);
        return totalAmount >= amount ? amount : totalAmount;
    }

    /**
     * 获取优惠比率 = 折扣价/总价
     *
     * @param discountAmount 折扣价
     * @param totalAmount    总价
     * @return 优惠比率
     */
    static BigDecimal getDiscountRate(Long discountAmount, Long totalAmount) {
        Assert.notNull(discountAmount);
        Assert.notNull(totalAmount);
        if (discountAmount >= totalAmount) {
            return BigDecimal.ONE;
        }
        return BigDecimal.valueOf(discountAmount).divide(BigDecimal.valueOf(totalAmount), DEFAULT_SCALE, RoundingMode.DOWN);
    }


    /**
     * 根据商品总价与商品数量 计算平均价
     *
     * @param totalAmount 总价
     * @param num         商品数量
     * @param mode        舍入模式
     * @return 平均价
     */
    static Long avgAmount(Long totalAmount, Integer num, RoundingMode mode) {
        return BigDecimal.valueOf(totalAmount)
                .divide(BigDecimal.valueOf(num), -2, mode)
                .longValue();
    }

    /**
     * 根据商品总价与商品数量 计算平均价
     *
     * @param totalAmount 总价
     * @param num         商品数量
     * @param mode        舍入模式
     * @return 平均价
     */
    static Long avgAmountNoScale(Long totalAmount, Integer num, RoundingMode mode) {
        return BigDecimal.valueOf(totalAmount)
                .divide(BigDecimal.valueOf(num), mode)
                .longValue();
    }

}

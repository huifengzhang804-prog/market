package com.medusa.gruul.order.api.helper;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.global.model.helper.AmountCalculateHelper;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 优惠金额计算 计算到每个item上的金额
 * discountTotalAmount 优惠总金额
 * discountItems       优惠作用的商品项
 *
 * @author 张治保
 * @since date 2023/9/13
 */
@Getter
@ToString
public class DiscountBuilder implements Serializable {
    /**
     * 折扣作用的商品项
     */
    private final List<ShopOrderItem> discountItems = new ArrayList<>();

    /**
     * 折扣金额
     */
    private long discountTotalAmount;

    DiscountBuilder() {
    }


    /**
     * 添加商品项
     *
     * @param item 商品项
     * @return this
     */
    public DiscountBuilder addItem(ShopOrderItem item) {
        if (item == null) {
            return this;
        }
        discountItems.add(item);
        return this;
    }

    /**
     * 添加商品项
     *
     * @param items 商品项
     * @return this
     */
    public DiscountBuilder addItems(List<ShopOrderItem> items) {
        if (CollUtil.isEmpty(items)) {
            return this;
        }
        discountItems.addAll(items);
        return this;
    }

    /**
     * 设置优惠总金额
     *
     * @param discountTotalAmount 优惠总金额
     * @return this
     */
    public DiscountBuilder discountTotalAmount(long discountTotalAmount) {
        this.discountTotalAmount = discountTotalAmount;
        return this;
    }

    /**
     * 计算优惠金额 不直接修改item的成交价与修正价 仅计算优惠金额 避免产生误用
     * 1.先按没中商品项的成交金额占总金额的比例计算优惠金额 并重新计算成交价与修正价
     * 2.如果优惠金额还有剩余，则按修正价扣除优惠金额
     * 3.如果优惠金额还有剩余，则按成交价扣除优惠金额 并重新计算成交价与修正价
     *
     * @return 每个商品对应的优惠信息
     */
    public Map<ShopProductSkuKey, ItemDiscount> toDiscount() {
        if (discountTotalAmount <= 0 || CollUtil.isEmpty(discountItems)) {
            return Map.of();
        }
        //初始化剩余未使用的优惠金额
        long unUsedDiscountAmount = discountTotalAmount;
        //获取所有商品项的总金额
        long totalAmount = discountItems.stream()
                .mapToLong(ShopOrderItem::payPrice)
                .sum();
        if (totalAmount <= 0) {
            return Map.of();
        }
        Map<ShopProductSkuKey, ItemDiscount> result = new HashMap<>(discountItems.size());
        //优先使用 成交金额占总金额的比例进行计算折扣金额
        for (ShopOrderItem item : discountItems) {
            if (unUsedDiscountAmount <= 0) {
                break;
            }
            //计算当前item总价
            //已经精确到分
            long currentAmount = item.payPrice();
            if (currentAmount == 0) {
                continue;
            }
            //计算当前item使用的折扣金额 按总金额占比计算
            //精确到分
            long currentDiscountAmount = AmountCalculateHelper.getDiscountAmount(discountTotalAmount, currentAmount, totalAmount);
            currentDiscountAmount = Math.min(currentAmount, currentDiscountAmount);
            unUsedDiscountAmount -= currentDiscountAmount;
            currentAmount -= currentDiscountAmount;
            //平均到每个商品 精确到分
            long dealPrice = AmountCalculateHelper.avgAmount(currentAmount, item.getNum(), RoundingMode.DOWN);
            //currentAmount 精确到分 - dealPrice也是精确到分 *整数  结果也精确到了分
            long fixPrice = currentAmount - dealPrice * item.getNum();
            //将计算结果放入map中
            result.computeIfAbsent(item.shopProductSkuKey(), key -> ItemDiscount.init(item.getId(), item.getNum()))
                    .set(currentDiscountAmount, dealPrice, fixPrice);
            item.setDealPrice(dealPrice)
                    .setFixPrice(fixPrice);
        }
        //剩余金额小于0时 说明优惠金额已经用完 直接返回
        if (unUsedDiscountAmount <= 0) {
            return result;
        }
        //优先处理 修正金额 修正金额作为基准
        for (ShopOrderItem item : discountItems) {
            if (unUsedDiscountAmount <= 0) {
                break;
            }
            long fixPrice = item.getFixPrice();
            if (fixPrice == 0) {
                continue;
            }
            //取修正金额和剩余优惠金额的最小值 作为当前item能使用的优惠金额
            long currentDiscountAmount = Math.min(fixPrice, unUsedDiscountAmount);
            fixPrice -= currentDiscountAmount;
            unUsedDiscountAmount -= currentDiscountAmount;
            result.computeIfAbsent(item.shopProductSkuKey(), key -> ItemDiscount.init(item.getId(), item.getNum()))
                    .set(currentDiscountAmount, item.getDealPrice(), fixPrice);
            item.setFixPrice(fixPrice);
        }
        //剩余金额小于0时 说明优惠金额已经用完 直接返回
        if (unUsedDiscountAmount <= 0) {
            return result;
        }
        //修正金额处理完毕 此时所有的修正金额都已为0， 处理成交金额
        for (ShopOrderItem item : discountItems) {
            if (unUsedDiscountAmount <= 0) {
                break;
            }
            //计算当前item总价
            long currentAmount = item.getDealPrice() * item.getNum();
            if (currentAmount == 0) {
                continue;
            }
            //计算当前item使用的折扣金额 按剩余折扣金额计算
            long currentDiscountAmount = Math.min(currentAmount, unUsedDiscountAmount);
            unUsedDiscountAmount -= currentDiscountAmount;
            currentAmount -= currentDiscountAmount;
            long dealPrice = AmountCalculateHelper.avgAmount(currentAmount, item.getNum(), RoundingMode.DOWN);
            long fixPrice = currentAmount - dealPrice * item.getNum();
            result.computeIfAbsent(item.shopProductSkuKey(), key -> ItemDiscount.init(item.getId(), item.getNum()))
                    .set(currentDiscountAmount, dealPrice, fixPrice);
            item.setDealPrice(dealPrice)
                    .setFixPrice(fixPrice);
        }
        return result;
    }

}
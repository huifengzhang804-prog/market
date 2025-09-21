package com.medusa.gruul.addon.distribute.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.BooleanUtil;
import com.medusa.gruul.addon.distribute.model.Bonus;
import com.medusa.gruul.addon.distribute.model.DistributeConstant;
import com.medusa.gruul.addon.distribute.model.enums.DistributorIdentity;
import com.medusa.gruul.addon.distribute.model.enums.ShareType;
import com.medusa.gruul.addon.distribute.mp.entity.DistributeProduct;
import com.medusa.gruul.addon.distribute.mp.entity.Distributor;
import com.medusa.gruul.addon.distribute.mp.entity.DistributorOrder;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.redis.util.RedisUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 张治保
 * date 2022/11/18
 */
public interface DistributeUtil {

    /**
     * 计算当前数量占比的佣金
     *
     * @param bonus      拆分前佣金
     * @param preNum     拆分之前数量
     * @param currentNum 当前数量
     * @return 当前数量占比的佣金
     */
    static long calculateBonus(Long bonus, Integer preNum, Integer currentNum) {
        if (bonus == null || bonus <= 0) {
            return 0;
        }
        long currentBonus = BigDecimal.valueOf(bonus)
                .multiply(BigDecimal.valueOf(currentNum))
                .divide(BigDecimal.valueOf(preNum), 2, RoundingMode.HALF_UP)
                .setScale(-2, RoundingMode.DOWN)
                .longValue();
        return currentBonus <= 0 ? 0 : currentBonus;
    }


    /**
     * 获取用户ID与佣金对应的列表
     *
     * @param distributorOrders 涉及的分销订单
     * @return 用户id与佣金对应列表
     */
    static Map<Long, Long> getUserIdAndBonusMap(List<DistributorOrder> distributorOrders) {
        if (CollUtil.isEmpty(distributorOrders)) {
            return Collections.emptyMap();
        }
        Map<Long, Long> bonusMap = new HashMap<>(CommonPool.NUMBER_EIGHT);
        for (DistributorOrder order : distributorOrders) {
            long one = order.getOne();
            if (BooleanUtil.isTrue(order.getPurchase()) && one > 0) {
                Long userId = order.getUserId();
                bonusMap.put(userId, bonusMap.getOrDefault(userId, 0L) + one);
            }
            Long oneId = order.getOneId();
            if (oneId != null && one > 0) {
                bonusMap.put(oneId, bonusMap.getOrDefault(oneId, 0L) + one);
            }
            long two = order.getTwo();
            Long twoId = order.getTwoId();
            if (twoId != null && two > 0) {
                bonusMap.put(twoId, bonusMap.getOrDefault(twoId, 0L) + two);
            }
            long three = order.getThree();
            Long threeId = order.getThreeId();
            if (threeId != null && three > 0) {
                bonusMap.put(threeId, bonusMap.getOrDefault(threeId, 0L) + three);
            }
        }
        return bonusMap;
    }

    /**
     * 获取分销商佣金调整分布式锁key
     *
     * @param userId 分销商用户id
     * @return 分布式锁key
     */
    static String getDistributorBonusKey(Long userId) {
        return RedisUtil.getLockKey(DistributeConstant.DISTRIBUTOR_BONUS_LOCK_KEY, userId);
    }

    /**
     * 店铺id 拼接 商品id
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 拼接字符串
     */
    static String getShopProductKey(Long shopId, Long productId) {
        return shopId + StrPool.DASHED + productId;
    }

    /**
     * 根据当前分销层级与总金额 获取 一到三级的佣金
     *
     * @param distributor       当前分销员信息
     * @param purchase          是否允许内购
     * @param num               商品数量
     * @param dealPrice         当前商品成交单价
     * @param distributeProduct 当前分销商品
     * @return 当前商品一到三级的佣金
     */
    static Bonus getBonus(Distributor distributor, boolean purchase, int num, long dealPrice, DistributeProduct distributeProduct) {
        boolean isPurchase = purchase && DistributorIdentity.AFFAIRS == distributor.getIdentity();
        Bonus bonus = new Bonus();
        bonus.setPurchase(isPurchase);
        if (dealPrice <= 0) {
            return bonus;
        }
        ShareType shareType = distributeProduct.getShareType();
        Long oneId = distributor.getOne();
        bonus.setOneId(oneId)
                .setOne(oneId == null && !isPurchase ? 0 : DistributeUtil.getCurrentLevelBonus(num, dealPrice, shareType, distributeProduct.getOne()));
        Long twoId = distributor.getTwo();
        bonus.setTwoId(twoId)
                .setTwo(twoId == null ? 0 : DistributeUtil.getCurrentLevelBonus(num, dealPrice, shareType, distributeProduct.getTwo()));
        Long threeId = distributor.getThree();
        return bonus.setThreeId(threeId)
                .setThree(threeId == null ? 0 : DistributeUtil.getCurrentLevelBonus(num, dealPrice, shareType, distributeProduct.getThree()));

    }

    /**
     * 获取订单商品sku的分布式锁 的key
     *
     * @param orderNo   订单
     * @param shopId    店铺id
     * @param productId 商品id
     * @param skuId     sku id
     * @return 分布式锁key
     */
    static String getSkuLockKey(String orderNo, Long shopId, Long productId, Long skuId) {
        return RedisUtil.key(DistributeConstant.ORDER_STATUS_UPDATE_LOCK_KEY, orderNo, shopId, productId, skuId);
    }

    /**
     * 获取当前等级的佣金
     *
     * @param num         成交数量
     * @param dealPrice   成交单价
     * @param shareType   佣金分摊类型
     * @param shareConfig 分摊值
     * @return 佣金
     */
    static long getCurrentLevelBonus(int num, long dealPrice, ShareType shareType, Long shareConfig) {
        if (shareConfig == null || shareConfig <= 0) {
            return 0;
        }
        return switch (shareType) {
            case FIXED_AMOUNT -> shareConfig * num;
            // 总金额 x 分佣比 / 1000000
            case RATE -> BigDecimal.valueOf(num * dealPrice)
                    .multiply(BigDecimal.valueOf(shareConfig))
                    .divide(BigDecimal.valueOf(CommonPool.UNIT_CONVERSION_TEN_THOUSAND * CommonPool.UNIT_CONVERSION_HUNDRED), -2, RoundingMode.DOWN)
                    .longValue();
            default -> CommonPool.NUMBER_ZERO;
        };
    }
}

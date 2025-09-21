package com.medusa.gruul.addon.full.reduction.util;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.addon.full.reduction.model.FullReductionConstant;
import com.medusa.gruul.addon.full.reduction.model.bo.FullReductionRule;
import com.medusa.gruul.addon.full.reduction.model.bo.MaxFullReduction;
import com.medusa.gruul.addon.full.reduction.model.enums.ProductType;
import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.redis.util.RedisUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.util.*;

/**
 * @author 张治保
 * @since 2024/6/15
 */
public interface FullReductionUtil {

    /**
     * 满减活动缓存 key
     *
     * @param shopId     店铺 id
     * @param activityId 活动 id
     * @return key
     */
    static String activityKey(Long shopId, Long activityId) {
        return RedisUtil.key(FullReductionConstant.FULL_REDUCTION_CACHE_KEY, shopId, activityId);
    }

    /**
     * 缓存满减活动规则
     */
    static void cacheActivity(
            Long shopId,
            Long activityId,
            List<FullReductionRule> rules,
            ProductType type,
            Set<Long> productIds,
            Duration cacheTime
    ) {
        RedisUtil.setCacheMap(
                activityKey(shopId, activityId),
                new ActivityInfo()
                        .setActivityId(activityId)
                        .setRules(rules)
                        .setProductType(type)
                        .setProductIds(productIds),
                cacheTime
        );
    }

    /**
     * 移除满减活动缓存
     *
     * @param shopId     店铺 id
     * @param activityId 活动 id
     */
    static void deleteActivity(Long shopId, Long activityId) {
        RedisUtil.delete(activityKey(shopId, activityId));
    }

    /**
     * @param shopId           店铺 id
     * @param productAmountMap 商品 id 和 金额的映射
     * @return 满减折扣力度最高的数据
     */

    @SuppressWarnings("unchecked")
    static MaxFullReduction maxFullReduction(Long shopId, Map<Long, Long> productAmountMap) {
        Set<String> activityKeys = RedisUtil.keys(RedisUtil.key(FullReductionConstant.FULL_REDUCTION_CACHE_KEY, shopId, "*"));
        if (CollUtil.isEmpty(activityKeys)) {
            return new MaxFullReduction();
        }
        //查询出当前店铺所有的满减活动
        List<Object> activitySource = RedisUtil.executePipelined(
                redisTemplate -> {
                    for (String activityKey : activityKeys) {
                        redisTemplate.opsForHash().entries(activityKey);
                    }
                }
        );
        List<ActivityInfo> activities = activitySource.stream()
                .map(source -> FastJson2.convert(source, ActivityInfo.class))
                .filter(info -> info.getRules() != null)
                .toList();
        if (CollUtil.isEmpty(activities)) {
            return new MaxFullReduction();
        }
        //根据 productType 和 productIds 过滤出参与了满减活动的商品 和其对应的最优满减 金额
        MaxFullReduction result = new MaxFullReduction();
        result.setDiscountAmount(0L);
        for (ActivityInfo activity : activities) {
            ProductType productType = activity.getProductType();
            Set<Long> effectProductIds = switch (productType) {
                //全部
                case ALL_PRODUCT -> productAmountMap.keySet();
                //交集
                case SPECIFIED_PRODUCT_PARTICIPATE ->
                        new HashSet<>(CollUtil.intersection(activity.getProductIds(), activity.getProductIds()));
                //差集
                default -> new HashSet<>(CollUtil.subtract(productAmountMap.keySet(), activity.getProductIds()));
            };
            //没有参与满减活动的商品
            if (CollUtil.isEmpty(effectProductIds)) {
                continue;
            }
            Long allProductAmount = getAllProductAmount(effectProductIds, productAmountMap);
            //当前活动满减规则
            List<FullReductionRule> rules = activity.getRules();

            // 遍历所有满减活动规则
            for (FullReductionRule rule : rules) {
                //计算满减金额
                Long newDiscountAmount = rule.getDiscountAmount(allProductAmount);
                //如果当前满减金额大于之前的满减金额 则更新
                if (result.getDiscountAmount() > newDiscountAmount) {
                    continue;
                }
                result.setActivityId(activity.getActivityId())
                        .setDiscountAmount(newDiscountAmount)
                        .setDesc(rule.getDesc())
                        .setProductIds(effectProductIds)
                        .setTotalAmount(allProductAmount);
            }
        }
        return result;
    }

    static Long getAllProductAmount(Set<Long> productIds, Map<Long, Long> productAmountMap) {
        return productIds.stream()
                .map(productAmountMap::get)
                .filter(Objects::nonNull)
                .reduce(0L, Long::sum);
    }


    @Getter
    @Setter
    @ToString
    @Accessors(chain = true)
    class ActivityInfo {
        private Long activityId;
        private List<FullReductionRule> rules;
        private ProductType productType;
        private Set<Long> productIds;
    }


}

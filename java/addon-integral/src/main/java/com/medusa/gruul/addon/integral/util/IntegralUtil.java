package com.medusa.gruul.addon.integral.util;

import com.medusa.gruul.addon.integral.model.enums.GainRuleType;
import com.medusa.gruul.common.redis.util.RedisUtil;

import java.time.LocalDate;

/**
 * 积分Util
 *
 * @author xiaoq
 * @Description 积分Util
 * @date 2023-02-01 09:46
 */
public final class IntegralUtil {

    private static final String INTEGRAL_PRODUCT_INFO_CACHE_KEY = "gruul:mall:integral:product";

    private static final String INTEGRAL_BEHAVIOR_CACHE_KEY = "gruul:mall:integral:behavior";

    private IntegralUtil() {
    }

    /**
     * 获取积分商品详情缓存信息
     *
     * @param id 积分商品id
     * @return 积分商品详情key
     */
    public static String integralProductCacheKey(Long id) {
        return RedisUtil.key(INTEGRAL_PRODUCT_INFO_CACHE_KEY, id);
    }

    /**
     * 获取积分行为缓存信息
     *
     * @param userId       用户id
     * @param gainRuleType 获得类型
     * @author shishuqian
     */
    public static String integralBehaviorCacheKey(Long userId, GainRuleType gainRuleType) {
        String nowDate = LocalDate.now().toString();
        return RedisUtil.key(INTEGRAL_BEHAVIOR_CACHE_KEY, userId, gainRuleType.toString(), nowDate);
    }


}

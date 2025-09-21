package com.medusa.gruul.addon.integral.util;

import com.medusa.gruul.addon.integral.model.constant.IntegralConstant;
import com.medusa.gruul.addon.integral.mp.entity.IntegralOrder;
import com.medusa.gruul.common.redis.util.RedisUtil;

import java.time.Duration;

/**
 * @author shishuqian
 * date 2023/2/2
 * time 14:57
 **/
public class IntegralOrderUtil {


    /**
     * 获取积分订单缓存信息
     *
     * @param userId          用户id
     * @param integralOrderNo 积分订单号
     * @return 订单缓存信息
     */
    public static IntegralOrder getCacheIntegralOrder(Long userId, String integralOrderNo) {
        return RedisUtil.getCacheMap(
                RedisUtil.key(IntegralConstant.INTEGRAL_ORDER_CACHE_KEY, userId, integralOrderNo),
                IntegralOrder.class
        );
    }


    /**
     * 设置积分订单缓存
     *
     * @param integralOrder 积分订单信息
     */
    public static void setCacheIntegralOrder(IntegralOrder integralOrder) {
        RedisUtil.setCacheMap(
                RedisUtil.key(IntegralConstant.INTEGRAL_ORDER_CACHE_KEY, integralOrder.getBuyerId(), integralOrder.getNo()),
                integralOrder,
                Duration.ofSeconds(20 * 60)
        );
    }

    /**
     * 删除积分订单缓存
     *
     * @param userId          用户id
     * @param integralOrderNo 积分订单号
     * @author shishuqian
     */
    public static void deleteIntegralOrderCache(Long userId, String integralOrderNo) {
        RedisUtil.delete(RedisUtil.key(IntegralConstant.INTEGRAL_ORDER_CACHE_KEY, userId, integralOrderNo));
    }

}

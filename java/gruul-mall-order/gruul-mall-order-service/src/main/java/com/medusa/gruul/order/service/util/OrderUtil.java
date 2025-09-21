package com.medusa.gruul.order.service.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.order.api.constant.OrderConstant;
import com.medusa.gruul.order.api.entity.Order;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.model.wechat.logistics.LogisticsType;
import com.medusa.gruul.order.api.pojo.SkuStock;
import com.medusa.gruul.order.service.model.bo.OrderDetailInfoBO;
import com.medusa.gruul.storage.api.bo.StSvBo;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 订单工具类
 *
 * @author 张治保
 * date 2022/6/9
 */
public interface OrderUtil {


    String PLATFORM = "platform";

    /**
     * 微信小程序物流模式
     *
     * @param distributionMode 下单配送方式
     * @return 物流模式
     */
    static LogisticsType getLogisticsType(DistributionMode distributionMode) {
        return switch (distributionMode) {
            case INTRA_CITY_DISTRIBUTION -> LogisticsType.INTRA_CITY_DISTRIBUTION;
            case EXPRESS -> LogisticsType.EXPRESS;
            case SHOP_STORE -> LogisticsType.SELF_DELIVERY;
            case VIRTUAL -> LogisticsType.VIRTUAL_PRODUCT;
        };
    }

    /**
     * 店铺商品列表转成 库存消费需要的格式
     *
     * @param activityType   活动类型
     * @param activityId     活动id
     * @param shopOrderItems 店铺商品列表
     * @return 库存消费需要的格式
     */
    static Map<ActivityShopProductSkuKey, StSvBo> toSkuKeyStSvMap(OrderType activityType, Long activityId, List<ShopOrderItem> shopOrderItems) {
        Map<ActivityShopProductSkuKey, StSvBo> result = MapUtil.newHashMap(shopOrderItems.size());
        for (ShopOrderItem shopOrderItem : shopOrderItems) {
            ActivityShopProductSkuKey key = (ActivityShopProductSkuKey) new ActivityShopProductSkuKey()
                    .setSkuId(shopOrderItem.getSkuId())
                    .setProductId(shopOrderItem.getProductId())
                    .setShopId(shopOrderItem.getShopId())
                    .setActivityType(activityType)
                    .setActivityId(activityId);
            StSvBo stSvBo = result.computeIfAbsent(key, curKey -> new StSvBo());
            Integer curNum = shopOrderItem.getNum();
            stSvBo.incrementStock(-curNum);
            stSvBo.incrementSales(curNum);
        }
        return result;
    }

    /**
     * 获取订单缓存信息
     *
     * @param orderNo 订单号
     * @param userId  用户id
     * @return 订单缓存信息
     */
    static Order getCacheOrder(Long userId, String orderNo) {
        return RedisUtil.getCacheMap(
                RedisUtil.key(OrderConstant.ORDER_CACHE_KEY, userId, orderNo),
                Order.class
        );
    }

    /**
     * 缓存订单信息
     *
     * @param userId     用户id
     * @param order      订单信息
     * @param expireTime 过期时间
     */
    static void cacheOrder(Long userId, Order order, Duration expireTime) {
        RedisUtil.setCacheMap(
                RedisUtil.key(OrderConstant.ORDER_CACHE_KEY, userId, order.getNo()),
                order,
                expireTime
        );
    }

    /**
     * 删除缓存的订单信息
     *
     * @param userId  用户id
     * @param orderNo 订单号
     */
    static void deleteOrderCache(Long userId, String orderNo) {
        RedisUtil.delete(RedisUtil.key(OrderConstant.ORDER_CACHE_KEY, userId, orderNo));
    }

    /**
     * 批量删除
     *
     * @param orderKeys 订单key列表
     */
    static void deletedOrderCacheBatch(List<OrderDetailInfoBO> orderKeys) {
        if (CollUtil.isEmpty(orderKeys)) {
            return;
        }
        RedisUtil.delete(
                orderKeys.stream()
                        .map(orderKey -> RedisUtil.key(OrderConstant.ORDER_CACHE_KEY, orderKey.getBuyerId(), orderKey.getOrderNo()))
                        .collect(Collectors.toSet())
        );
    }

    /**
     * 将items 转成 skustock
     *
     * @param shopOrderItems 店铺商品列表
     * @return 库存消费需要的格式
     */
    static List<SkuStock> toSkuStocks(List<ShopOrderItem> shopOrderItems) {
        return CollUtil.emptyIfNull(shopOrderItems).stream()
                .map(shopOrderItem -> new SkuStock()
                        .setShopId(shopOrderItem.getShopId())
                        .setProductId(shopOrderItem.getProductId())
                        .setSkuId(shopOrderItem.getSkuId())
                        .setNum(shopOrderItem.getNum())
                ).toList();
    }

    /**
     * 生成取货码
     *
     * @param mode 取货码业务模式
     * @return 取货码
     */
    static Long pickupCode(Long shopId, DistributionMode mode) {
        RedisTemplate<String, Object> redisTemplate = RedisUtil.getRedisTemplate();
        LocalDate date = LocalDate.now();
        String key = RedisUtil.key(OrderConstant.PICKUP_CODE_CACHE, mode, shopId, date);
        Long result = redisTemplate.opsForValue().increment(key);
        if (result != null && result.equals(1L)) {
            redisTemplate.expire(
                    key,
                    Duration.between(
                            LocalDateTime.now(),
                            date.atTime(LocalTime.MAX)
                    )
            );
        }
        return result;
    }

}

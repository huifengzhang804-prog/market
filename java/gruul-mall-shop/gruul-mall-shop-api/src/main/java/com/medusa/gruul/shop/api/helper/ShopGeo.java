package com.medusa.gruul.shop.api.helper;

import cn.hutool.core.util.IdUtil;
import com.medusa.gruul.shop.api.constant.ShopConstant;
import com.vividsolutions.jts.geom.Point;
import org.springframework.dao.DataAccessException;
import org.springframework.data.geo.Distance;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author 张治保
 * @since 2024/8/24
 */
public interface ShopGeo {

    /**
     * 新增一个 店铺定位
     *
     * @param redisTemplate redisTemplate
     * @param shopId        店铺 id
     * @param location      店铺定位
     */
    static void add(RedisTemplate<String, Object> redisTemplate, Long shopId, Point location) {
        redisTemplate.opsForGeo()
                .add(
                        ShopConstant.SHOP_GEO,
                        toPoint(location),
                        shopId
                );
    }

    /**
     * 批量新增店铺定位
     *
     * @param redisTemplate   redisTemplate
     * @param shopLocationMap 店铺与其定位 map key 店铺 id, value店铺定位
     */
    static void batchAdd(RedisTemplate<String, Object> redisTemplate, Map<Long, Point> shopLocationMap) {
        if (shopLocationMap == null || shopLocationMap.isEmpty()) {
            return;
        }
        Map<Object, org.springframework.data.geo.Point> pointMap = convertMap(
                shopLocationMap,
                key -> key,
                ShopGeo::toPoint
        );
        redisTemplate.opsForGeo()
                .add(ShopConstant.SHOP_GEO, pointMap);
    }

    /**
     * 批量新增店铺定位
     *
     * @param redisTemplate redisTemplate
     * @param shopIds       店铺 id 集合
     */
    static void batchRemove(RedisTemplate<String, Object> redisTemplate, Set<Long> shopIds) {
        redisTemplate.opsForGeo()
                .remove(
                        ShopConstant.SHOP_GEO,
                        shopIds.toArray(Long[]::new)
                );
    }


    /**
     * 当前位置到指定店铺的距离 单位 米
     *
     * @param redisTemplate redisTemplate
     * @param location      当前位置
     * @param shopId        店铺 id
     * @return 位置到指定店铺的距离
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static Distance distance(RedisTemplate<String, Object> redisTemplate, Point location, Long shopId) {
        List<Object> pipelined = redisTemplate.executePipelined(
                new SessionCallback<>() {
                    @Override
                    public <K, V> Object execute(@NonNull RedisOperations<K, V> operations) throws DataAccessException {
                        String member = IdUtil.fastSimpleUUID();
                        GeoOperations geo = operations.opsForGeo();
                        geo.add(ShopConstant.SHOP_GEO, toPoint(location), member);
                        geo.distance(ShopConstant.SHOP_GEO, shopId, member, RedisGeoCommands.DistanceUnit.METERS);
                        geo.remove(ShopConstant.SHOP_GEO, member);
                        return null;
                    }
                }
        );
        Object distance = pipelined.get(1);
        if (distance == null) {
            throw new RuntimeException("shop geo not existed");
        }
        return ((Distance) distance);
    }


    static <K, K2, V, U> Map<K2, U> convertMap(Map<K, V> map, Function<K, K2> keyMapper, Function<V, ? extends U> valueMapper) {
        Map<K2, U> result = new HashMap<>(map.size());
        map.forEach(
                (key, value) -> result.put(keyMapper.apply(key), valueMapper.apply(value))
        );
        return result;
    }

    static org.springframework.data.geo.Point toPoint(Point position) {
        return new org.springframework.data.geo.Point(position.getX(), position.getY());
    }


}

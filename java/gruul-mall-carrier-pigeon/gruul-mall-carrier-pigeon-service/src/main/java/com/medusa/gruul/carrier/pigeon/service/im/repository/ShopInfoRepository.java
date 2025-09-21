package com.medusa.gruul.carrier.pigeon.service.im.repository;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.medusa.gruul.carrier.pigeon.service.im.constants.IMRedisConstant;
import com.medusa.gruul.carrier.pigeon.service.im.entity.ShopInfo;
import com.medusa.gruul.common.redis.util.RedisUtil;
import org.springframework.stereotype.Repository;

/**
 * 基于Redis实现的店铺信息持久层
 * @author An.Yan
 */
@Repository
public class ShopInfoRepository {

    /**
     * 保存店铺信息到Redis
     * @param shopInfo 店铺信息,参考 {@link ShopInfo}
     */
    public void saveShopInfo(ShopInfo shopInfo) {
        RedisUtil.getRedisTemplate().opsForHash().put(
                IMRedisConstant.IM_SHOP_INFO_KEY,
                String.valueOf(shopInfo.getShopId()), JSON.toJSONString(shopInfo));
    }

    /**
     * 根据ID获取店铺信息
     * @param shopId 店铺ID
     * @return {@link ShopInfo}
     */
    public ShopInfo getShopInfoById(Long shopId) {
        Object shopInfo = RedisUtil.getRedisTemplate().opsForHash().get(IMRedisConstant.IM_SHOP_INFO_KEY, String.valueOf(shopId));
        if (shopInfo == null) {
            return null;
        }
        return JSONObject.parseObject(String.valueOf(shopInfo), ShopInfo.class);
    }
}

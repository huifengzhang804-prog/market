package com.medusa.gruul.addon.seckill.addon;

import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.goods.api.model.vo.ActivityDetailVO;
import com.medusa.gruul.order.api.addon.activity.ActivityParam;
import com.medusa.gruul.order.api.addon.activity.ActivityResp;

/**
 * @author WuDi date: 2022/11/22
 */
public interface AddonSecKillProvider {


    String FILTER = "SPIKE";

    /**
     * 秒杀活动预计算
     *
     * @param param 秒杀活动参数
     * @return 秒杀活动信息
     */
    ActivityResp seckillBudget(ActivityParam param);

    /**
     * 获取秒杀活动
     *
     * @param param 秒杀活动参数
     * @return 秒杀活动信息
     */
    ActivityResp activity(ActivityParam param);


    /**
     * 获取秒杀活动商品详情
     *
     * @param activityId 秒杀活动id
     * @param userId     用户 id
     * @param key        商品 sku key
     * @return 秒杀活动商品详情
     */
    ActivityDetailVO activity(Long activityId, Long userId, ShopProductSkuKey key);
}

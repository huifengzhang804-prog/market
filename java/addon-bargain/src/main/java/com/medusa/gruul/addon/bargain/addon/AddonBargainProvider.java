package com.medusa.gruul.addon.bargain.addon;

import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.goods.api.model.vo.ActivityDetailVO;
import com.medusa.gruul.order.api.addon.activity.ActivityParam;
import com.medusa.gruul.order.api.addon.activity.ActivityResp;

/**
 * @author wudi
 */
public interface AddonBargainProvider {

    String FILTER = "BARGAIN";


    /**
     * 砍价活动订单价格预算
     *
     * @param param 活动请求参数
     * @return 砍价活动信息
     */
    ActivityResp bargainBudget(ActivityParam param);

    /**
     * 获取砍价活动
     *
     * @param param 活动请求参数
     * @return 砍价活动信息
     */
    ActivityResp getBargain(ActivityParam param);

    /**
     * 获取拼团活动商品详情
     *
     * @param activityId 秒杀活动id
     * @param userId     用户 id
     * @param key        商品 sku key
     * @return 秒杀活动商品详情
     */
    ActivityDetailVO activity(Long activityId, Long userId, ShopProductSkuKey key);
}

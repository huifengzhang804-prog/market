package com.medusa.gruul.addon.team.addon;

import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.goods.api.model.vo.ActivityDetailVO;
import com.medusa.gruul.order.api.addon.activity.ActivityParam;
import com.medusa.gruul.order.api.addon.activity.ActivityResp;

/**
 * @author 张治保
 * date 2023/3/16
 */
public interface AddonTeamProvider {

    String FILTER = "TEAM";

    /**
     * 拼团活动预计算
     *
     * @param param 拼团活动参数
     * @return 拼团活动信息
     */
    ActivityResp teamBudget(ActivityParam param);

    /**
     * 拼团活动订单
     *
     * @param param 拼团活动参数
     * @return 拼团活动信息
     */
    ActivityResp teamOrder(ActivityParam param);


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

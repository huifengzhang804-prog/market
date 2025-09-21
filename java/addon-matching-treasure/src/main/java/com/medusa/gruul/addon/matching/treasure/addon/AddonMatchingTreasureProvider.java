package com.medusa.gruul.addon.matching.treasure.addon;

import com.medusa.gruul.goods.api.model.vo.SetMealBasicInfoVO;
import com.medusa.gruul.order.api.addon.activity.ActivityParam;
import com.medusa.gruul.order.api.addon.activity.ActivityResp;

import java.util.List;

public interface AddonMatchingTreasureProvider {

    String FILTER = "PACKAGE";

    /**
     * 套餐活动商品订单价格预计算
     *
     * @param param 套餐订单参数
     * @return 套餐活动数据
     */
    ActivityResp packageBudget(ActivityParam param);

    /**
     * 套餐活动商品下单
     *
     * @param param 套餐订单参数
     * @return 套餐活动商品数据
     */
    ActivityResp getMatchingTreasure(ActivityParam param);

    /**
     * 商品详情页套餐基本信息
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 套餐基本信息
     */
    List<SetMealBasicInfoVO> getSetMealBasicInfo(Long shopId, Long productId);

}

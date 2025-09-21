package com.medusa.gruul.addon.matching.treasure.service;

import com.medusa.gruul.addon.matching.treasure.model.vo.SetMealDetailVO;
import com.medusa.gruul.goods.api.model.vo.SetMealBasicInfoVO;

import java.util.List;

public interface SetMealConsumerService {

    /**
     * 商品详情页套餐基本信息
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 套餐基本信息
     */
    List<SetMealBasicInfoVO> getSetMealBasicInfo(Long shopId, Long productId);

    /**
     * 套餐详情
     * @param shopId    店铺id
     * @param setMealId 套餐id
     * @return 套餐详情
     */
    SetMealDetailVO setMealDetail(Long shopId, Long setMealId);
}

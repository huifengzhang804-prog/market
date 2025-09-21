package com.medusa.gruul.addon.full.reduction.addon;

import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.goods.api.model.vo.ProductDiscountVO;
import com.medusa.gruul.order.api.addon.fullreduction.FullReductionResponse;
import com.medusa.gruul.order.api.addon.fullreduction.OrderFullReductionParam;
import jakarta.annotation.Nullable;

public interface AddonFullReductionProvider {

    String FILTER = "FULL";

    /**
     * 获取满减可用的优惠列表
     *
     * @param orderFullReductionParam 订单满减参数
     * @return 满减活动对应的优惠列表
     */
    FullReductionResponse getFullReductionsOrderDiscount(OrderFullReductionParam orderFullReductionParam);

    /**
     * 商品详情满减优惠
     *
     * @param userId 用户id
     * @param key    商品 key
     * @param amount 商品金额
     * @return 商品折扣信息
     */
    ProductDiscountVO discount(@Nullable Long userId, ShopProductKey key, Long amount);
}

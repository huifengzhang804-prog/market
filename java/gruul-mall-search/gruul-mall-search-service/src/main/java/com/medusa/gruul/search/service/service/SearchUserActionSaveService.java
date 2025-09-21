package com.medusa.gruul.search.service.service;

import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.medusa.gruul.shop.api.model.dto.ShopOperationDTO;

/**
 * 检索用户行为保存Service
 *
 * @author xiaoq
 * @Description  SearchUserActionSaveService.java
 * @date 2023-12-08 14:56
 */
public interface SearchUserActionSaveService {
    /**
     * 保存检查信息店铺 (搜过的店)
     * @param shopOperation 店铺操作数据
     */
    void saveSearchShop(ShopOperationDTO shopOperation);

    /**
     * 保存支付订单的店铺数据(买过的店)
     *
     * @param orderPaidBroadcast 订单操作数据
     */
    void savePayShop(OrderPaidBroadcastDTO orderPaidBroadcast);

    /**
     * 保存用户关注店铺信息(关注过的店)
     *
     * @param shopOperation 店铺操作数据
     */
    void saveUserAttentionShop(ShopOperationDTO shopOperation);

    /**
     * 保存用户浏览店铺信息(看过的店)
     * @param shopId 店铺id
     * @param userId 用户id
     */
    void userFootMarkAdd(Long shopId, Long userId);

}

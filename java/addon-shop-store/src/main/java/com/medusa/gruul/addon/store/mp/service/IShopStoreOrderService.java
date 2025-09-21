package com.medusa.gruul.addon.store.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.store.model.vo.ShopStoreTransactionSummaryVO;
import com.medusa.gruul.addon.store.mp.entity.ShopStoreOrder;

/**
 * 店铺门店订单服务层
 *
 * @author xiaoq
 * @Description 店铺门店订单服务层
 * @date 2023-03-16 18:27
 */
public interface IShopStoreOrderService extends IService<ShopStoreOrder> {
    /**
     * 获取店铺门店交易汇总
     *
     * @param storeId 门店id
     * @param shopId 店铺id
     * @return 店铺门店交易汇总
     */
    ShopStoreTransactionSummaryVO getStoreTransactionSummary(Long storeId, Long shopId);
}

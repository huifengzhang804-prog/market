package com.medusa.gruul.addon.store.service;

import com.medusa.gruul.addon.store.model.vo.ShopStoreAssistantVO;

/**
 *
 *
 * @author xiaoq
 * @Description 店铺门店通用Serice
 * @date 2023-03-08 16:13
 */
public interface CommonShopStoreService {
    /**
     * 根据id 获取门店信息详情
     * @param id 门店id
     * @param shopId 店铺id
     * @return 门店 店员VO
     */
    ShopStoreAssistantVO getShopStoreInfoById(Long id, Long shopId);
}

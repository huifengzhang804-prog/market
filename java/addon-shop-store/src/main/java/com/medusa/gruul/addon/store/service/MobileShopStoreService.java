package com.medusa.gruul.addon.store.service;

import com.medusa.gruul.addon.store.model.dto.ShopStoreDistanceDTO;
import com.medusa.gruul.addon.store.model.vo.ShopStoreDistanceVO;
import com.medusa.gruul.addon.store.mp.entity.ShopStore;

import java.util.List;

/**
 * 移动店铺门店服务层
 *
 * @author xiaoq
 * @Description 移动店铺门店服务层
 * @date 2023-03-10 16:15
 */
public interface MobileShopStoreService {
    /**
     *  获取门店信息 by 门店店员手机号
     *
     * @param shopAssistantPhone 门店店员手机号
     * @return 门店详情
     */
    ShopStore getShopStoreByShopAssistantPhone(String shopAssistantPhone);

    /**
     * 获取门店距离TOP3
     *
     * @param shopStoreDistance 门店店铺距离
     * @return  店铺门店信息
     */
    List<ShopStoreDistanceVO> getStoreDistance(ShopStoreDistanceDTO shopStoreDistance);

    /**
     *  获取店铺门店提货时间
     *
     * @param id 店铺门店id
     * @param shopId  店铺id
     * @return 店铺门店信息
     */
    ShopStore getStoreOptionalDeliveryTime(Long id, Long shopId);
}

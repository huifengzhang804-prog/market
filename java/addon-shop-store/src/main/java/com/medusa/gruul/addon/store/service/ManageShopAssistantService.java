package com.medusa.gruul.addon.store.service;

import com.medusa.gruul.addon.store.model.dto.ShopAssistantDTO;
import com.medusa.gruul.addon.store.model.vo.ShopAssistantVO;

import java.util.List;

/**
 * @author xiaoq
 * @Description 管理店铺店员服务层
 * @date 2023-03-14 14:52
 */
public interface ManageShopAssistantService {
    /**
     * 店铺店员新增
     *
     * @param shopAssistant 店铺店员DTO
     * @param shopId        店铺id
     */
    void issueShopAssistant(ShopAssistantDTO shopAssistant, Long shopId);

    /**
     * 获取店铺店员列表
     *
     * @return 店铺店员列表
     */
    List<ShopAssistantVO> getShopAssistantList();

    /**
     * 给店铺设置门店
     *
     * @param storeId         门店id
     * @param shopAssistantId 店员id
     */
    void setStore(Long storeId, Long shopAssistantId);

    /**
     * 删除店员
     *
     * @param shopAssistantId 店员id
     */
    void delShopAssistant( Long shopAssistantId);
}

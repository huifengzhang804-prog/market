package com.medusa.gruul.addon.store.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.store.model.vo.ShopAssistantVO;
import com.medusa.gruul.addon.store.mp.entity.ShopAssistant;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2023-03-14 14:55
 */
public interface IShopAssistantService extends IService<ShopAssistant> {
    /**
     * 获取店员列表 及关联门店名称VO
     * @param shopId 店铺id
     * @return List<ShopAssistantVO>
     */
    List<ShopAssistantVO> getShopAssistantList(Long shopId);
}

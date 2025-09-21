package com.medusa.gruul.addon.store.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.store.model.param.ShopStoreParam;
import com.medusa.gruul.addon.store.model.vo.ShopStoreAssistantVO;
import com.medusa.gruul.addon.store.model.vo.ShopStoreListVO;
import com.medusa.gruul.addon.store.mp.entity.ShopStore;

/**
 * @author xiaoq
 * @Description 店铺门店服务层
 * @date 2023-03-07 17:51
 */
public interface IShopStoreService extends IService<ShopStore> {
    /**
     * 店铺门店列表
     *
     * @param shopStoreParam 查询参数
     * @return IPage<店铺门店列表>
     */
    IPage<ShopStoreListVO> getShopStoreListVO(ShopStoreParam shopStoreParam);

    /**
     *  获取门店详情信息 及所属门店店员
     *
     * @param id 门店id
     * @param shopId 店铺id
     * @return 门店详情信息
     */
    ShopStoreAssistantVO getShopStoreInfoById(Long id, Long shopId);
}

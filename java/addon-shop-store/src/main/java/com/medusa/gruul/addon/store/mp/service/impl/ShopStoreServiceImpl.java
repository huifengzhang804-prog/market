package com.medusa.gruul.addon.store.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.store.model.param.ShopStoreParam;
import com.medusa.gruul.addon.store.model.vo.ShopStoreAssistantVO;
import com.medusa.gruul.addon.store.model.vo.ShopStoreListVO;
import com.medusa.gruul.addon.store.mp.entity.ShopStore;
import com.medusa.gruul.addon.store.mp.mapper.ShopStoreMapper;
import com.medusa.gruul.addon.store.mp.service.IShopStoreService;
import org.springframework.stereotype.Service;

/**
 *
 *
 * @author xiaoq
 * @Description 店铺门店服务实现层
 * @date 2023-03-07 17:53
 */
@Service
public class ShopStoreServiceImpl extends ServiceImpl<ShopStoreMapper, ShopStore>implements IShopStoreService {
    @Override
    public IPage<ShopStoreListVO> getShopStoreListVO(ShopStoreParam shopStoreParam) {
        return this.baseMapper.queryShopStoreListVO(shopStoreParam);
    }

    @Override
    public ShopStoreAssistantVO getShopStoreInfoById(Long id, Long shopId) {
        return this.baseMapper.queryShopStoreInfo(id,shopId);
    }
}

package com.medusa.gruul.addon.store.service.impl;

import com.medusa.gruul.addon.store.model.vo.ShopStoreAssistantVO;
import com.medusa.gruul.addon.store.mp.service.IShopStoreService;
import com.medusa.gruul.addon.store.service.CommonShopStoreService;
import com.medusa.gruul.global.model.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author xiaoq
 * @Description 店铺门店通用实现
 * @date 2023-03-08 16:14
 */
@Service
@RequiredArgsConstructor
public class CommonShopStoreServiceImpl implements CommonShopStoreService {

    private final IShopStoreService shopStoreService;

    @Override
    public ShopStoreAssistantVO getShopStoreInfoById(Long id, Long shopId) {
        ShopStoreAssistantVO shopStoreInfo = shopStoreService.getShopStoreInfoById(id, shopId);
        if (shopStoreInfo == null){
            throw new GlobalException("当前门店信息不存在");
        }
        return shopStoreInfo;
    }
}

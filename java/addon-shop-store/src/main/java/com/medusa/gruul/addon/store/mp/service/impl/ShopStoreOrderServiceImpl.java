package com.medusa.gruul.addon.store.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.store.model.vo.ShopStoreTransactionSummaryVO;
import com.medusa.gruul.addon.store.mp.entity.ShopStoreOrder;
import com.medusa.gruul.addon.store.mp.mapper.ShopStoreOrderMapper;
import com.medusa.gruul.addon.store.mp.service.IShopStoreOrderService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2023-03-16 18:28
 */
@Service
public class ShopStoreOrderServiceImpl extends ServiceImpl<ShopStoreOrderMapper, ShopStoreOrder> implements IShopStoreOrderService {
    @Override
    public ShopStoreTransactionSummaryVO getStoreTransactionSummary(Long storeId, Long shopId) {
        return this.baseMapper.queryStoreTransactionSummary(storeId, shopId);
    }
}

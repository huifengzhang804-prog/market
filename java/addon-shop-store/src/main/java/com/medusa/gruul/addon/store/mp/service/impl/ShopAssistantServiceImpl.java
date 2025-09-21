package com.medusa.gruul.addon.store.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.store.model.vo.ShopAssistantVO;
import com.medusa.gruul.addon.store.mp.entity.ShopAssistant;
import com.medusa.gruul.addon.store.mp.mapper.ShopAssistantMapper;
import com.medusa.gruul.addon.store.mp.service.IShopAssistantService;
import org.springframework.stereotype.Service;

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
@Service
public class ShopAssistantServiceImpl extends ServiceImpl<ShopAssistantMapper, ShopAssistant> implements IShopAssistantService {
    @Override
    public List<ShopAssistantVO> getShopAssistantList(Long shopId) {
        return this.baseMapper.queryShopAssistantList(shopId);
    }
}

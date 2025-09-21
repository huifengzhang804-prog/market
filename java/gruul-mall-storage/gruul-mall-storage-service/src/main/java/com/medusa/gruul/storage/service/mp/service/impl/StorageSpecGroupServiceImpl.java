package com.medusa.gruul.storage.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.storage.api.vo.ProductSpecVO;
import com.medusa.gruul.storage.service.mp.entity.StorageSpecGroup;
import com.medusa.gruul.storage.service.mp.mapper.StorageSpecGroupMapper;
import com.medusa.gruul.storage.service.mp.service.IStorageSpecGroupService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 商品规格组 服务实现类
 *
 * @author 张治保
 * @since 2022-07-12
 */
@Service
public class StorageSpecGroupServiceImpl extends ServiceImpl<StorageSpecGroupMapper, StorageSpecGroup> implements IStorageSpecGroupService {

    @Override
    public List<ProductSpecVO> getProductSpecs(Long shopId, Long productId) {
        return baseMapper.getProductSpecs(shopId, productId);
    }

    @Override
    public Set<StorageSpecGroup> getGroupsBatch(Set<ShopProductKey> keys) {
        return baseMapper.getGroupsBatch(keys);
    }
}

package com.medusa.gruul.storage.service.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.storage.api.vo.ProductSpecVO;
import com.medusa.gruul.storage.service.mp.entity.StorageSpecGroup;

import java.util.List;
import java.util.Set;

/**
 * 商品规格组 服务类
 *
 * @author 张治保
 * @since 2022-07-12
 */
public interface IStorageSpecGroupService extends IService<StorageSpecGroup> {

    /**
     * 查询商品规格组
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 商品规格组
     */
    List<ProductSpecVO> getProductSpecs(Long shopId, Long productId);

    /**
     * 批量查询规格组与规格信息
     *
     * @param keys 店铺与商品ID集合
     * @return 查询结果
     */

    Set<StorageSpecGroup> getGroupsBatch(Set<ShopProductKey> keys);
}

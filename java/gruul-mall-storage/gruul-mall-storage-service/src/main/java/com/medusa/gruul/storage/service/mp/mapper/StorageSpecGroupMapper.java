package com.medusa.gruul.storage.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.storage.api.vo.ProductSpecVO;
import com.medusa.gruul.storage.service.mp.entity.StorageSpecGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 商品规格组 Mapper 接口
 *
 * @author 张治保
 * @since 2022-07-12
 */
public interface StorageSpecGroupMapper extends BaseMapper<StorageSpecGroup> {

    /**
     * 查询商品规格组
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 商品规格组
     */
    List<ProductSpecVO> getProductSpecs(@Param("shopId") Long shopId, @Param("productId") Long productId);


    /**
     * 批量查询规格组与规格信息
     *
     * @param keys 店铺与商品ID集合
     * @return 查询结果
     */

    Set<StorageSpecGroup> getGroupsBatch(@Param("keys") Set<ShopProductKey> keys);
}

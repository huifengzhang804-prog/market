package com.medusa.gruul.goods.service.mp.mapper;

import com.medusa.gruul.goods.api.entity.ProductCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.goods.api.model.CategoryLevel;
import com.medusa.gruul.goods.api.model.vo.CategoryLevelName;
import com.medusa.gruul.goods.api.model.vo.ProductFirstCategoryVO;
import com.medusa.gruul.goods.api.model.vo.ShopProductCategoryLevel1WithNumVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 *
 * 商品类目 Mapper 接口
 *
 * @author xiaoq
 * @since 2022-03-04
 */
public interface ProductCategoryMapper extends BaseMapper<ProductCategory> {

    /**
     * 获取类目级别Name
     *
     * @param shopCategory 类目级别id
     * @return  类目级别Name
     */
    CategoryLevelName queryCategoryLevelName(@Param("shopCategory") CategoryLevel shopCategory);

    /**
     * 获取与店铺ID匹配的category数据
     *
     * @param shopID 店铺ID
     * @param level  {@link CategoryLevel}
     * @return {@link ProductCategory}
     */
    List<ProductCategory> queryProductCategoriesByLevel(@Param("shopID")Long shopID, @Param("level") com.medusa.gruul.common.custom.aggregation.classify.enums.CategoryLevel level);

    /**
     * 获取指定的店铺集合所对应的一级分类下，所有三级分类的商品数量.
     *
     * @param shopIDList 不重复的店铺集合
     * @return {@link ProductFirstCategoryVO}
     */
    List<ShopProductCategoryLevel1WithNumVO> queryProductCategoriesByShopIDList(@Param("shopIDList") Set<Long> shopIDList);
}

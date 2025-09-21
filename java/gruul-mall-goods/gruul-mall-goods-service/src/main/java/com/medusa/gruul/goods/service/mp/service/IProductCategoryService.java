package com.medusa.gruul.goods.service.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.goods.api.entity.ProductCategory;
import com.medusa.gruul.goods.api.model.CategoryLevel;
import com.medusa.gruul.goods.api.model.vo.CategoryLevelName;
import com.medusa.gruul.goods.api.model.vo.ShopProductCategoryLevel1WithNumVO;
import java.util.List;
import java.util.Set;

/**
 * 商品类目信息 服务类
 *
 * @author xiaoq
 * @since 2022-03-04
 */
public interface IProductCategoryService extends IService<ProductCategory> {

    /**
     * 根据类目级别id 获取对应的类目名称
     *
     * @param shopCategory 店铺类目级别id
     * @return 类目级别名称
     */
    CategoryLevelName getCategoryLevelName(CategoryLevel shopCategory);

    /**
     * 获取与店铺ID匹配的category数据
     *
     * @param shopID 店铺ID
     * @param level  {@link CategoryLevel}
     * @return {@link ProductCategory}
     */
    List<ProductCategory> queryProductCategoriesByLevel(Long shopID,
            com.medusa.gruul.common.custom.aggregation.classify.enums.CategoryLevel level);

    /**
     * 获取指定的店铺集合所对应的一级分类下，所有三级分类的商品数量.
     *
     * @param shopIDList 不重复的店铺集合
     * @return {@link ShopProductCategoryLevel1WithNumVO}
     */
    List<ShopProductCategoryLevel1WithNumVO> queryProductCategoriesByShopIDList(Set<Long> shopIDList);
}

package com.medusa.gruul.goods.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.goods.api.entity.ProductCategory;
import com.medusa.gruul.goods.api.model.CategoryLevel;
import com.medusa.gruul.goods.api.model.vo.CategoryLevelName;
import com.medusa.gruul.goods.api.model.vo.ProductFirstCategoryVO;
import com.medusa.gruul.goods.api.model.vo.ShopProductCategoryLevel1WithNumVO;
import com.medusa.gruul.goods.service.mp.mapper.ProductCategoryMapper;
import com.medusa.gruul.goods.service.mp.service.IProductCategoryService;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * 商品类目信息 服务实现类
 *
 * @author xiaoq
 * @since 2022-03-04
 */
@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements
        IProductCategoryService {

    @Override
    public CategoryLevelName getCategoryLevelName(CategoryLevel shopCategory) {
        return this.baseMapper.queryCategoryLevelName(shopCategory);
    }

    /**
     * 获取与店铺ID匹配的category数据
     *
     * @param shopID 店铺ID
     * @param level  {@link CategoryLevel}
     * @return {@link ProductCategory}
     */
    @Override
    public List<ProductCategory> queryProductCategoriesByLevel(Long shopId,
            com.medusa.gruul.common.custom.aggregation.classify.enums.CategoryLevel level) {
        return this.baseMapper.queryProductCategoriesByLevel(shopId, level);
    }

    /**
     * 获取指定的店铺集合所对应的一级分类下，所有三级分类的商品数量.
     *
     * @param shopIdList 不重复的店铺集合
     * @return {@link ProductFirstCategoryVO}
     */
    @Override
    public List<ShopProductCategoryLevel1WithNumVO> queryProductCategoriesByShopIDList(Set<Long> shopIdList) {
        return this.baseMapper.queryProductCategoriesByShopIDList(shopIdList);
    }


}

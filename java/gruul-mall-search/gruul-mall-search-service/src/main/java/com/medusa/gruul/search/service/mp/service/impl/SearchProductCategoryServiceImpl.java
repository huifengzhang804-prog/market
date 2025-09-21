package com.medusa.gruul.search.service.mp.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.custom.aggregation.classify.enums.CategoryLevel;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.goods.api.entity.ProductCategory;
import com.medusa.gruul.search.service.model.vo.SearchBrandDetailVO;
import com.medusa.gruul.search.service.mp.mapper.SearchProductCategoryMapper;
import com.medusa.gruul.search.service.mp.service.IProductCategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 商品类目信息 服务实现类
 *
 * @author xiaoq
 * @since 2022-03-04
 */
@Service
@DS("goods")
public class SearchProductCategoryServiceImpl extends ServiceImpl<SearchProductCategoryMapper, ProductCategory> implements IProductCategoryService {

    private static SearchBrandDetailVO.CategoryVO getCategoryVO(Long categoryId, CategoryLevel categoryLevel) {
        return new SearchBrandDetailVO.CategoryVO()
                .setCategoryLevel(categoryLevel)
                .setId(categoryId);
    }

    /**
     * 获取二级、一级分类id
     *
     * @param categoryThirdId 三级分类id
     * @return 三级分类集合
     */
    @Override
    public List<SearchBrandDetailVO.CategoryVO> getProductCategory(Long categoryThirdId) {
        ProductCategory productCategoryThird = this.getProductCategory(categoryThirdId, CategoryLevel.LEVEL_3);
        if (productCategoryThird == null) {
            return Collections.emptyList();
        }
        ProductCategory productCategorySecond = this.getProductCategory(productCategoryThird.getParentId(), CategoryLevel.LEVEL_2);
        List<SearchBrandDetailVO.CategoryVO> categoryVOs = new ArrayList<>(CommonPool.NUMBER_THREE);
        categoryVOs.add(getCategoryVO(categoryThirdId, CategoryLevel.LEVEL_3));
        categoryVOs.add(getCategoryVO(productCategorySecond.getId(), CategoryLevel.LEVEL_2));
        categoryVOs.add(getCategoryVO(productCategorySecond.getParentId(), CategoryLevel.LEVEL_1));
        return categoryVOs;
    }

    private ProductCategory getProductCategory(Long categoryId, CategoryLevel categoryLevel) {
        return this.lambdaQuery()
                .eq(ProductCategory::getId, categoryId)
                .eq(ProductCategory::getLevel, categoryLevel)
                .one();
    }
}

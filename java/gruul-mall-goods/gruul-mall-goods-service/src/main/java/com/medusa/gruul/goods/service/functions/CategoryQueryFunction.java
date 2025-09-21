package com.medusa.gruul.goods.service.functions;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.custom.aggregation.classify.enums.CategoryLevel;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.goods.api.entity.ProductCategory;
import com.medusa.gruul.goods.service.mp.service.IProductCategoryService;
import com.medusa.gruul.goods.service.mp.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2022/4/21
 */
@Component
@RequiredArgsConstructor
public class CategoryQueryFunction {

    private final IProductCategoryService productCategoryService;

    private final IProductService productService;

    /**
     * 查询下一级分类 并且设置到对应的父及分类的children下
     */
    public List<ProductCategory> setChildren(List<ProductCategory> categories, CategoryLevel level) {
        List<ProductCategory> childrenCategory = this.children(
                categories.stream().map(ProductCategory::getId).collect(Collectors.toSet()),
                level
        );
        Map<Long, List<ProductCategory>> secondCategoriesMap = this.groupByParentId(childrenCategory);
        categories.forEach(
                primaryCategory -> {
                    List<ProductCategory> children = secondCategoriesMap.get(primaryCategory.getId());
                    if (CollUtil.isEmpty(children)) {
                        return;
                    }
                    primaryCategory.setChildren(children);
                }
        );
        return childrenCategory;
    }


    /**
     * 根据分类parentIds 查询子分类列表
     */
    public List<ProductCategory> children(Set<Long> parentIds, CategoryLevel level) {
        return productCategoryService.lambdaQuery()
                .eq(ProductCategory::getLevel, level)
                .in(ProductCategory::getParentId, parentIds)
                .orderByAsc(ProductCategory::getSort)
                .list();

    }

    /**
     * 根据父id分组
     */
    public Map<Long, List<ProductCategory>> groupByParentId(List<ProductCategory> categories) {
        return categories.stream().collect(
                Collectors.groupingBy(
                        ProductCategory::getParentId
                )
        );
    }

    /**
     * 查询分类id和下面所有子分类id
     *
     * @param categoryId 当前分类id
     * @param level      当前分类级别
     * @return 查询结果
     */
    public Set<Long> categoryIdAndChildrenIds(Long categoryId, CategoryLevel level) {
        Set<Long> categoryIdAndChildrenIds = new HashSet<>() {{
            add(categoryId);
        }};
        switch (level) {
            case LEVEL_3:
                return categoryIdAndChildrenIds;
            case LEVEL_2:
                categoryIdAndChildrenIds.addAll(getChildrenIds(categoryId));
                break;
            case LEVEL_1:
                Set<Long> childrenIds = getChildrenIds(categoryId);
                categoryIdAndChildrenIds.addAll(childrenIds);
                childrenIds.forEach(
                        childrenId -> categoryIdAndChildrenIds.addAll(
                                categoryIdAndChildrenIds(childrenId, CategoryLevel.LEVEL_2)
                        )
                );
                break;
            default:
                throw SystemCode.DATA_NOT_EXIST.exception();
        }
        return categoryIdAndChildrenIds;
    }

    /**
     * 获取当前分类下的子分类id 集合
     *
     * @param categoryId 当前分类id
     * @return 查询结果
     */
    private Set<Long> getChildrenIds(Long categoryId) {
        return productCategoryService.lambdaQuery()
                .select(ProductCategory::getId)
                .eq(ProductCategory::getParentId, categoryId)
                .list()
                .stream()
                .map(ProductCategory::getId)
                .collect(Collectors.toSet());
    }
}

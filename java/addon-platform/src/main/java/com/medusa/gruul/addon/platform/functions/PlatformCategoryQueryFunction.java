package com.medusa.gruul.addon.platform.functions;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.addon.platform.mp.entity.PlatformCategory;
import com.medusa.gruul.addon.platform.mp.service.IPlatformCategoryService;
import com.medusa.gruul.common.custom.aggregation.classify.enums.CategoryLevel;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
public class PlatformCategoryQueryFunction {
    private final IPlatformCategoryService platformCategoryService;


    /**
     * 查询下一级分类 并且设置到对应的父及分类的children下
     */
    public List<PlatformCategory> setChildren(List<PlatformCategory> categories, CategoryLevel level) {
        List<PlatformCategory> childrenCategory = this.children(categories.stream().map(BaseEntity::getId).collect(Collectors.toSet()), level);
        Map<Long, List<PlatformCategory>> secondCategoriesMap = this.groupByParentId(childrenCategory);
        categories.forEach(
                primaryCategory -> {
                    List<PlatformCategory> children = secondCategoriesMap.get(primaryCategory.getId());
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
    public List<PlatformCategory> children(Set<Long> parentIds, CategoryLevel level) {
        return platformCategoryService.lambdaQuery()
                .select(PlatformCategory::getId, 
                       PlatformCategory::getName, 
                       PlatformCategory::getParentId, 
                       PlatformCategory::getLevel, 
                       PlatformCategory::getSort, 
                       PlatformCategory::getCategoryImg, 
                       PlatformCategory::getDeductionRatio, 
                       PlatformCategory::getAds)
                .eq(PlatformCategory::getLevel, level)
                .in(PlatformCategory::getParentId, parentIds)
                .orderByAsc(PlatformCategory::getSort)
                .list();
    }


    /**
     * 根据父id分组
     */
    public Map<Long, List<PlatformCategory>> groupByParentId(List<PlatformCategory> categories) {
        return categories.stream().collect(
                Collectors.groupingBy(
                        PlatformCategory::getParentId
                )
        );
    }
}

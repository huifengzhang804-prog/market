package com.medusa.gruul.search.service.mp.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.custom.aggregation.classify.enums.CategoryLevel;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.search.service.model.vo.SearchBrandDetailVO;
import com.medusa.gruul.search.service.mp.entity.PlatformCategory;
import com.medusa.gruul.search.service.mp.mapper.SearchCategoryMapper;
import com.medusa.gruul.search.service.mp.service.IPlatformCategoryService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: WuDi
 * @date: 2022/10/28
 */
@Service
@DS("addon-platform")
@ConditionalOnProperty(prefix = "gruul", name = "mode", havingValue = "B2B2C", matchIfMissing = true)
public class SearchPlatformCategoryServiceImpl extends ServiceImpl<SearchCategoryMapper, PlatformCategory> implements IPlatformCategoryService {


    /**
     * 根据三级类目id获取二级类目和一级类目id
     *
     * @param parentCategoryId 三级类目id
     * @return 平台三级类目
     */
    @Override
    public List<SearchBrandDetailVO.CategoryVO> getPlatformCategory(Long parentCategoryId) {
        PlatformCategory platformCategory = this.lambdaQuery()
                .eq(PlatformCategory::getLevel, CategoryLevel.LEVEL_3)
                .eq(PlatformCategory::getId, parentCategoryId)
                .one();
        if (platformCategory == null) {
            return null;
        }
        PlatformCategory level2Category = this.lambdaQuery()
                .eq(PlatformCategory::getLevel, CategoryLevel.LEVEL_2)
                .eq(PlatformCategory::getId, platformCategory.getParentId())
                .one();
        List<SearchBrandDetailVO.CategoryVO> categoryVOs = new ArrayList<>(CommonPool.NUMBER_THREE);
        categoryVOs.add(newSearchCategoryVO(CategoryLevel.LEVEL_3, parentCategoryId));
        categoryVOs.add(newSearchCategoryVO(CategoryLevel.LEVEL_2, level2Category.getId()));
        categoryVOs.add(newSearchCategoryVO(CategoryLevel.LEVEL_1, level2Category.getParentId()));
        return categoryVOs;
    }

    private SearchBrandDetailVO.CategoryVO newSearchCategoryVO(CategoryLevel level, Long id) {
        return new SearchBrandDetailVO.CategoryVO()
                .setCategoryLevel(level)
                .setId(id);
    }
}


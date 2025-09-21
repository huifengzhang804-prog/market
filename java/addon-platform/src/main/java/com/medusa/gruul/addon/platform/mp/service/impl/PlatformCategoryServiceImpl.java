package com.medusa.gruul.addon.platform.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.platform.mp.entity.PlatformCategory;
import com.medusa.gruul.addon.platform.mp.mapper.CategoryMapper;
import com.medusa.gruul.addon.platform.mp.service.IPlatformCategoryService;
import com.medusa.gruul.goods.api.model.CategoryLevel;
import com.medusa.gruul.goods.api.model.vo.CategoryLevelName;
import org.springframework.stereotype.Service;

/**
 * @author: WuDi
 * @date: 2022/10/28
 */
@Service
public class PlatformCategoryServiceImpl extends ServiceImpl<CategoryMapper, PlatformCategory> implements IPlatformCategoryService {


    @Override
    public CategoryLevelName getPlatformCategoryLevelName(CategoryLevel platformCategory) {
        return this.baseMapper.queryPlatformCategoryLevelName(platformCategory);
    }
}


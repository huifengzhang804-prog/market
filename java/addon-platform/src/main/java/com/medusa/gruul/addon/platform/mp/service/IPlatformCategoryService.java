package com.medusa.gruul.addon.platform.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.platform.mp.entity.PlatformCategory;
import com.medusa.gruul.goods.api.model.CategoryLevel;
import com.medusa.gruul.goods.api.model.vo.CategoryLevelName;

/**
 * @author: WuDi
 * @date: 2022/10/28
 */
public interface IPlatformCategoryService extends IService<PlatformCategory> {


    /**
     * 跟你平台类目id 获取平台类目信息
     *
     * @param platformCategory 平台类目id
     * @return CategoryLevelName.java
     */
    CategoryLevelName getPlatformCategoryLevelName(CategoryLevel platformCategory);

}

package com.medusa.gruul.addon.platform.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.platform.model.vo.CategoryVO;
import com.medusa.gruul.addon.platform.mp.entity.PlatformCategory;
import com.medusa.gruul.goods.api.model.CategoryLevel;
import com.medusa.gruul.goods.api.model.vo.CategoryLevelName;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 类目mapper
 *
 * @author xiaoq
 * @since 2022-04-18
 */
public interface CategoryMapper extends BaseMapper<PlatformCategory> {

    /**
     * 获取类目信息 By page
     * @param page 分页对象
     * @return 符合条件的类目信息
     */
    IPage<CategoryVO> getCategoryList(Page<?> page);

    /**
     *  根据平台类目一级id 获取所有3级类目id
     *
     * @param platformCategoryId  平台一级类目
     * @return 三级类目ids
     */
    List<Long> getLevelCategoryList(@Param("platformCategoryId") Long platformCategoryId);


    /**
     * 根据平台类目id获取对应平台类目名称
     *
     * @param platformCategory 平台类目id
     * @return CategoryLevelName
     */
    CategoryLevelName queryPlatformCategoryLevelName(@Param("platformCategory") CategoryLevel platformCategory);

}

package com.medusa.gruul.addon.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.platform.model.dto.CategoryDTO;
import com.medusa.gruul.addon.platform.model.dto.PlatformCategoryRankDTO;
import com.medusa.gruul.addon.platform.model.dto.PlatformCategorySortDTO;
import com.medusa.gruul.addon.platform.model.parpm.CategoryParam;
import com.medusa.gruul.addon.platform.model.vo.CategoryFirstIdWithNumVO;
import com.medusa.gruul.addon.platform.model.vo.CategoryVO;
import com.medusa.gruul.addon.platform.mp.entity.PlatformCategory;
import com.medusa.gruul.goods.api.model.vo.ApiProductVO;

import java.util.List;

/**
 * @author xiaoq
 * @date 04/18
 */
public interface PlatformCategoryService {
    /**
     * 新增一级类目
     *
     * @param categoryDto 类目dto
     */
    void addCategory(CategoryDTO categoryDto);


    /**
     * 类目修改
     *
     * @param categoryDto 类目Dto
     */
    void updateCategory(CategoryDTO categoryDto);


    /**
     * 类目删除
     *
     * @param id 类目id
     */
    void deleteCategory(Long id);


    /**
     * 获取类目信息 By page
     *
     * @param page 分页对象
     * @return 符合条件的类目信息
     */
    IPage<CategoryVO> getCategoryList(Page<?> page);


    /**
     * 平台类目排序
     *
     * @param platformCategorySortDto platformCategorySortDto
     */
    void updatePlatformCategorySort(PlatformCategorySortDTO platformCategorySortDto);

    /**
     * 获取类目信息 by param
     *
     * @param categoryParam 查询参数
     * @return 符合条件的类目信息
     */
    IPage<PlatformCategory> getCategoryListByLevel(CategoryParam categoryParam);

    /**
     * 获取一级平台类目id下得所有三级id
     *
     * @param platformCategoryId 平台分类id
     * @return List<三级分类id>
     */
    List<Long> getLevelCategoryList(Long platformCategoryId);

    /**
     * 获取类目信息 by ids
     *
     * @param categoryRank 查询dto
     * @return List<PlatformCategory>
     */
    List<PlatformCategory> getCategoryInfoByIds(PlatformCategoryRankDTO categoryRank);

    /**
     * 根据平台一级分类查看一级分类下的商品数量
     *
     * @param page 分页
     * @return 一级分类下的商品数量
     */
    IPage<CategoryFirstIdWithNumVO> getCategoryFirstIdWithNumVO(Page<PlatformCategory> page);

    /**
     * 获取商品信息 by categoryRank
     *
     * @param categoryRank  类目级别查询
     * @return   Page<ApiProductVO>
     */
    Page<ApiProductVO> getProductInfoByPlatformCategoryId(PlatformCategoryRankDTO categoryRank);
}

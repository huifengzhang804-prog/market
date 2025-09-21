package com.medusa.gruul.goods.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.custom.aggregation.classify.dto.CategoryRankDTO;
import com.medusa.gruul.goods.api.entity.ProductCategory;
import com.medusa.gruul.goods.api.model.vo.ProductCategoryLevel1WithNumVO;
import com.medusa.gruul.goods.service.model.dto.CategorySortDTO;
import com.medusa.gruul.goods.service.model.dto.ProductCategoryDTO;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 商品分类服务
 *
 * @author 张治保 date 2022/4/21
 */
public interface CategoryService {

    /**
     * 分页查询商品分类
     *
     * @param page 分页查询信息
     * @return 分页查询结果
     */
    IPage<ProductCategory> pageProductCategory(Page<Void> page);

    /**
     * 根据父id分页查询子分类列表
     *
     * @param parentId 父id
     * @param page     分页参数
     * @return 分页查询结果
     */
    IPage<ProductCategory> pageProductCategoryByParentId(Long parentId, Page<Void> page);

    /**
     * 新增商品分类
     *
     * @param category 分类信息
     */
    void newProductCategory(ProductCategoryDTO category);

    /**
     * 新增商品分类
     *
     * @param categoryId 分类id
     * @param category   分类信息
     */
    void editProductCategory(Long categoryId, ProductCategoryDTO category);

    /**
     * 根据分类id删除分类
     *
     * @param categoryId 分类id
     */
    void deleteProductCategory(Long categoryId);

    /**
     * 分类排序调整
     *
     * @param categorySort 排序信息
     */
    void sortCategories(CategorySortDTO categorySort);

    /**
     * 查询一级分类和一级分类下的商品数量
     *
     * @param page page
     * @return 分类和分类商品数量
     */
    IPage<ProductCategoryLevel1WithNumVO> pageCategoryLevel1WithProductNum(Page<Void> page);

    /**
     * 根据一级ids 获取 一级类目下的类目信息
     *
     * @param categoryRank 类目级别dto
     * @return List<ProductCategory>
     */
    List<ProductCategory> getCategoryInfoByIds(CategoryRankDTO categoryRank);

    /**
     * 查询一级分类和一级分类下的商品数量
     *
     * @param shopIds 店铺ID集合
     * @return 分类和分类商品数量
     */
    Map<Long, List<ProductCategoryLevel1WithNumVO>> pageCategoryLevel1WithProductNum(Set<Long> shopIds);

    /**
     * 查询商品类目
     *
     * @param shopId
     * @param categoryId
     * @return
     */
    ProductCategory getCategoryInfoByShopIdAndCategoryId(Long shopId, Long categoryId);
}

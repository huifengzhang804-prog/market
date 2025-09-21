package com.medusa.gruul.goods.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.custom.aggregation.classify.dto.CategoryRankDTO;
import com.medusa.gruul.common.custom.aggregation.classify.enums.CategoryLevel;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.entity.ProductCategory;
import com.medusa.gruul.goods.api.model.enums.GoodsError;
import com.medusa.gruul.goods.api.model.vo.ProductCategoryLevel1WithNumVO;
import com.medusa.gruul.goods.api.model.vo.ShopProductCategoryLevel1WithNumVO;
import com.medusa.gruul.goods.service.functions.CategoryQueryFunction;
import com.medusa.gruul.goods.service.model.dto.CategorySortDTO;
import com.medusa.gruul.goods.service.model.dto.ProductCategoryDTO;
import com.medusa.gruul.goods.service.mp.service.IProductCategoryService;
import com.medusa.gruul.goods.service.mp.service.IProductService;
import com.medusa.gruul.goods.service.service.CategoryService;
import com.medusa.gruul.goods.service.util.GoodsUtil;
import com.medusa.gruul.search.api.enums.CategoryCountType;
import com.medusa.gruul.search.api.model.CategoryCountParam;
import com.medusa.gruul.search.api.model.CategoryStaticVo;
import com.medusa.gruul.search.api.rpc.SearchRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author 张治保 date 2022/4/21
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final IProductService productService;
    private final CategoryQueryFunction categoryQueryFunction;
    private final IProductCategoryService productCategoryService;
    private SearchRpcService searchRpcService;

    @Override
    public IPage<ProductCategory> pageProductCategory(Page<Void> page) {
        /* 一级类目分页查询
         */
        IPage<ProductCategory> categoriesPage = productCategoryService.lambdaQuery()
                .eq(ProductCategory::getParentId, CommonPool.NUMBER_ZERO)
                .eq(ProductCategory::getLevel, CategoryLevel.LEVEL_1)
                .orderByAsc(ProductCategory::getSort)
                .page(new Page<>(page.getCurrent(), page.getSize()));

        /* 查询出的一级分类列表
         */
        List<ProductCategory> categories = categoriesPage.getRecords();
        if (CollUtil.isEmpty(categories)) {
            return categoriesPage;
        }
        /* 二级分类列表
         */
        categories = categoryQueryFunction.setChildren(categories, CategoryLevel.LEVEL_2);
        /* 三级分类列表
         */
        if (CollUtil.isEmpty(categories)) {
            return categoriesPage;
        }
        categoryQueryFunction.setChildren(categories, CategoryLevel.LEVEL_3);
        // 查询分类下商品数量
        Set<Long> categoryIds = categoriesPage.getRecords().stream().map(ProductCategory::getId)
                .collect(Collectors.toSet());
        Map<Long, CategoryStaticVo> categoryCountMap = searchRpcService.categoryCount(
                new CategoryCountParam()
                        .setType(CategoryCountType.SHOP)
                        .setShopId(ISystem.shopIdMust())
                        .setFirstIds(categoryIds)
        );
        CategoryStaticVo defaultValue = new CategoryStaticVo();
        categoriesPage.getRecords()
                .forEach(
                        first -> {
                            first.setProductNumber(categoryCountMap.getOrDefault(first.getId(), defaultValue).getProductCount());
                            CollUtil.emptyIfNull(first.getChildren())
                                    .forEach(
                                            second -> {
                                                second.setProductNumber(
                                                        categoryCountMap.getOrDefault(second.getId(), defaultValue).getProductCount());
                                                CollUtil.emptyIfNull(second.getChildren())
                                                        .forEach(third -> third.setProductNumber(
                                                                categoryCountMap.getOrDefault(third.getId(),
                                                                        defaultValue).getProductCount()));
                                            }
                                    );
                        }
                );
        return categoriesPage;
    }

    @Override
    public IPage<ProductCategory> pageProductCategoryByParentId(Long parentId, Page<Void> page) {
        return productCategoryService.lambdaQuery()
                .eq(ProductCategory::getParentId, parentId)
                .page(
                        new Page<>(page.getCurrent(), page.getSize())
                );
    }

    @Override
    public void newProductCategory(ProductCategoryDTO category) {
        category.paramCheck();
        category.save(productCategoryService);
    }

    @Override
    public void editProductCategory(Long categoryId, ProductCategoryDTO category) {
        category.paramCheck();
        category.update(productCategoryService, categoryId);
    }

    @Override
    public void deleteProductCategory(Long categoryId) {
        ProductCategory category = productCategoryService.getById(categoryId);
        SystemCode.DATA_NOT_EXIST.trueThrow(category == null);
        /* 查询所有子分类id (包含自身id)
         */
        Set<Long> categoryIds = categoryQueryFunction.categoryIdAndChildrenIds(categoryId, category.getLevel());
        /* 判断分类id列表下是否有绑定商品
         */
        boolean exists = productService.lambdaQuery()
                .in(Product::getCategoryId, categoryIds)
                .exists();
        GoodsError.ATTRIBUTES_REPETITION.trueThrow(exists);
        /* 删除数据
         */
        RedisUtil.doubleDeletion(() -> {
            boolean success = productCategoryService.lambdaUpdate()
                    .in(ProductCategory::getId, categoryIds)
                    .remove();
            SystemCode.DATA_UPDATE_FAILED.falseThrow(success);
        }, GoodsUtil.productCategoryCacheKey(ISystem.shopIdMust(), categoryId));

    }

    @Override
    public void sortCategories(CategorySortDTO categorySort) {
        List<Long> sortedIds = categorySort.getSortedIds();

        List<ProductCategory> categories = productCategoryService.lambdaQuery()
                .in(ProductCategory::getId, sortedIds)
                .eq(ProductCategory::getParentId, categorySort.getParentId())
                .last("ORDER BY FIELD (id," + sortedIds.stream().map(String::valueOf).collect(Collectors.joining(","))
                        + ")")
                .list();
        IntStream.range(0, categories.size()).forEach(
                index -> categories.get(index).setSort(index)
        );
        productCategoryService.updateBatchById(categories);
    }

    /**
     * 查询一级分类和一级分类下的商品数量
     *
     * @param page page
     * @return 分类和分类下商品数量
     */
    @Override
    public IPage<ProductCategoryLevel1WithNumVO> pageCategoryLevel1WithProductNum(Page<Void> page) {

        Page<ProductCategory> level1Page = productCategoryService.lambdaQuery()
                .eq(ProductCategory::getLevel, CategoryLevel.LEVEL_1)
                .orderByDesc(ProductCategory::getSort)
                .page(new Page<>(page.getCurrent(), page.getSize()));
        List<ProductCategory> level1List = level1Page.getRecords();
        if (CollUtil.isEmpty(level1List)) {
            return level1Page.convert(record -> null);
        }
        Map<Long, CategoryStaticVo> categoryCount = searchRpcService.categoryCount(
                new CategoryCountParam()
                        .setType(CategoryCountType.SHOP)
                        .setNormal(Boolean.TRUE)
                        .setShopId(ISystem.shopIdMust())
                        .setFirstIds(level1List.stream().map(ProductCategory::getId).collect(Collectors.toSet()))
        );
        IPage<ProductCategoryLevel1WithNumVO> resultPage = level1Page.convert(
                record -> {
                    ProductCategoryLevel1WithNumVO result = new ProductCategoryLevel1WithNumVO();
                    result.setId(record.getId())
                            .setName(record.getName())
                            .setProductNum(categoryCount.getOrDefault(record.getId(), new CategoryStaticVo()).getProductCount());
                    return result;
                }
        );
        resultPage.setRecords(
                resultPage.getRecords().stream().filter(record -> record.getProductNum() > 0).toList()
        );
        return resultPage;
    }

    /**
     * 根据一级ids 获取 一级类目下的类目信息
     *
     * @param categoryRank 类目级别dto
     * @return List<ProductCategory>
     */
    @Override
    public List<ProductCategory> getCategoryInfoByIds(CategoryRankDTO categoryRank) {
        List<ProductCategory> categories = productCategoryService.lambdaQuery()
                .eq(ProductCategory::getParentId, CommonPool.NUMBER_ZERO)
                .eq(ProductCategory::getLevel, CategoryLevel.LEVEL_1)
                .last("ORDER BY FIELD (id," + categoryRank.getIds().stream().map(String::valueOf)
                        .collect(Collectors.joining(",")) + ")")
                .in(BaseEntity::getId, categoryRank.getIds()).list();
        /* 查询出的一级分类列表
         */
        if (CollUtil.isEmpty(categories)) {
            return categories;
        }
        /* 二级分类列表
         */
        List<ProductCategory> categoriesSecond = categoryQueryFunction.setChildren(categories, CategoryLevel.LEVEL_2);
        /* 三级分类列表
         */
        if (CollUtil.isEmpty(categoriesSecond)) {
            return categories;
        }
        if (categoryRank.getCategoryLevel() == CategoryLevel.LEVEL_3) {
            categoryQueryFunction.setChildren(categoriesSecond, CategoryLevel.LEVEL_3);
        }
        return categories;
    }

    /**
     * 查询一级分类和一级分类下的商品数量
     *
     * @param shopIds 店铺ID
     * @return 分类和分类商品数量
     */
    @Override
    public Map<Long, List<ProductCategoryLevel1WithNumVO>> pageCategoryLevel1WithProductNum(Set<Long> shopIds) {

        List<ShopProductCategoryLevel1WithNumVO> shopProductCategoryLevel1WithNumVOS = productCategoryService.queryProductCategoriesByShopIDList(
                shopIds);
        if (CollUtil.isEmpty(shopProductCategoryLevel1WithNumVOS)) {
            return new HashMap<>();
        }
        return shopProductCategoryLevel1WithNumVOS
                .stream()
                .collect(Collectors.groupingBy(ShopProductCategoryLevel1WithNumVO::getShopID,
                        Collectors.collectingAndThen(Collectors.toList(),
                                item -> item
                                        .stream()
                                        .map(a ->
                                                new ProductCategoryLevel1WithNumVO()
                                                        .setProductNum(a.getProductNum())
                                                        .setId(a.getId())
                                                        .setName(a.getName())
                                        )
                                        .collect(Collectors.toList()))));

    }

    @Override
    public ProductCategory getCategoryInfoByShopIdAndCategoryId(Long shopId, Long categoryId) {
        String key = GoodsUtil.productCategoryCacheKey(shopId, categoryId);
        ProductCategory productCategory = RedisUtil.getCacheMap(ProductCategory.class,
                () -> TenantShop.disable(
                        () -> productCategoryService.lambdaQuery().eq(ProductCategory::getId, categoryId).one())
                , Duration.ofSeconds(RedisUtil.expireWithRandom(CommonPool.UNIT_CONVERSION_TEN_THOUSAND)),
                key);
        return productCategory;
    }

    @Autowired
    @Lazy
    public void setSearchRpcService(SearchRpcService searchRpcService) {
        this.searchRpcService = searchRpcService;
    }
}

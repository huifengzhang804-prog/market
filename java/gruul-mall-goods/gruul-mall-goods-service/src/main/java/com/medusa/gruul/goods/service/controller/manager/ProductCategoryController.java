package com.medusa.gruul.goods.service.controller.manager;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.custom.aggregation.classify.dto.CategoryRankDTO;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.web.valid.group.Create;
import com.medusa.gruul.common.web.valid.group.Update;
import com.medusa.gruul.goods.api.entity.ProductCategory;
import com.medusa.gruul.goods.api.model.vo.ProductCategoryLevel1WithNumVO;
import com.medusa.gruul.goods.service.model.dto.CategorySortDTO;
import com.medusa.gruul.goods.service.model.dto.ProductCategoryDTO;
import com.medusa.gruul.goods.service.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 商品展示分类 前端控制器
 *
 * @author xiaoq
 * @since 2022-03-04
 */
@RestController
@RequiredArgsConstructor
@PreAuthorize("@S.shopPerm('goods:list','goods:category')")
@RequestMapping("/goods/product/category")
public class ProductCategoryController {

    private final CategoryService categoryService;

    /**
     * 分页查询商品分类
     *
     * @param page 分页查询信息
     * @return 分页查询结果
     */
    @GetMapping
    @Log("分页查询商品分类")
    @PreAuthorize("permitAll()")
    public Result<IPage<ProductCategory>> pageProductCategory(Page<Void> page) {
        return Result.ok(
                categoryService.pageProductCategory(page)
        );
    }

    /**
     * 根据父id分页查询子分类列表
     *
     * @param parentId 父id
     */
    @GetMapping("/by/parent/{parentId}")
    @PreAuthorize("permitAll()")
    @Log("根据父id分页查询子分类列表")
    public Result<IPage<ProductCategory>> pageProductCategoryByParentId(@PathVariable Long parentId, Page<Void> page) {
        return Result.ok(
                categoryService.pageProductCategoryByParentId(parentId, page)
        );
    }

    /**
     * 新增商品分类
     *
     * @param category 商品分类
     */
    @Idem(500)
    @PostMapping
    @Log("新增商品分类")
    public Result<Void> newProductCategory(@RequestBody @Validated(Create.class) ProductCategoryDTO category) {
        categoryService.newProductCategory(category);
        return Result.ok();
    }

    /**
     * 编辑商品分类
     *
     * @param categoryId 分类id
     * @param category   分类
     */
    @Idem(500)
    @Log("编辑商品分类")
    @PutMapping("/{categoryId}")
    public Result<Void> editProductCategory(@PathVariable("categoryId") Long categoryId, @RequestBody @Validated(Update.class) ProductCategoryDTO category) {
        categoryService.editProductCategory(categoryId, category);
        return Result.ok();
    }

    /**
     * 删除商品分类
     *
     * @param categoryId 分类id
     */
    @Log("删除商品分类")
    @DeleteMapping("/{categoryId}")
    public Result<Void> deleteProductCategory(@PathVariable("categoryId") Long categoryId) {
        categoryService.deleteProductCategory(categoryId);
        return Result.ok();
    }

    /**
     * 调整排序
     *
     * @param categorySort categorySort
     */
    @Log("调整排序")
    @PatchMapping
    public Result<Void> sortCategories(@RequestBody @Valid CategorySortDTO categorySort) {
        categoryService.sortCategories(categorySort);
        return Result.ok();
    }

    /**
     * 查询一级分类和一级分类下商品的数量
     *
     * @param page 分页参数
     */
    @Log("查询一级分类和一级分类下商品的数量")
    @PreAuthorize("permitAll()")
    @GetMapping("/categoryLevel1WithProductNum")
    public Result<IPage<ProductCategoryLevel1WithNumVO>> pageCategoryLevel1WithProductNum(Page<Void> page) {
        return Result.ok(
                categoryService.pageCategoryLevel1WithProductNum(page)
        );
    }


    /**
     * 根据一级ids 获取 一级类目下的类目信息
     *
     * @param categoryRank 类目级别dto
     * @return List<ProductCategory>
     */
    @GetMapping("by/ids")
    @PreAuthorize("permitAll()")
    public Result<List<ProductCategory>> getCategoryInfoByIds(CategoryRankDTO categoryRank) {
        return Result.ok(categoryService.getCategoryInfoByIds(categoryRank));
    }


}

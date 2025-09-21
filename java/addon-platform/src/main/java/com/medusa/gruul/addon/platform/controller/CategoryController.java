package com.medusa.gruul.addon.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.platform.model.dto.CategoryDTO;
import com.medusa.gruul.addon.platform.model.dto.PlatformCategoryRankDTO;
import com.medusa.gruul.addon.platform.model.dto.PlatformCategorySortDTO;
import com.medusa.gruul.addon.platform.model.parpm.CategoryParam;
import com.medusa.gruul.addon.platform.model.vo.CategoryFirstIdWithNumVO;
import com.medusa.gruul.addon.platform.model.vo.CategoryVO;
import com.medusa.gruul.addon.platform.mp.entity.PlatformCategory;
import com.medusa.gruul.addon.platform.service.PlatformCategoryService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.goods.api.model.vo.ApiProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 类目信息
 *
 * @Description: 类目
 * @Author: xiaoq
 * @Date : 2022-04-18 13:48
 */
@RestController
@Validated
@RequiredArgsConstructor
@PreAuthorize("@S.platformPerm('sortManage')")
@RequestMapping("/platform/category")
public class CategoryController {

    private final PlatformCategoryService categoryService;

    /**
     * 类目信息保存
     *
     * @param categoryDto 类目DTO
     * @return Void
     */
    @Log
    @PostMapping("/save")
    public Result<Void> addCategory(@RequestBody @Validated CategoryDTO categoryDto) {
        categoryService.addCategory(categoryDto);
        return Result.ok();
    }


    /**
     * 类目信息修改
     *
     * @param categoryDto 类目Dto
     * @return Void
     */
    @Log
    @PutMapping("/update/{id}")
    public Result<Void> updateCategory(@RequestBody @Validated CategoryDTO categoryDto, @PathVariable(name = "id") Long id) {
        categoryDto.setId(id);
        categoryService.updateCategory(categoryDto);
        return Result.ok();
    }

    /**
     * 同级类目排序
     *
     * @param platformCategorySortDto 类目排序信息
     * @return Void
     */
    @PutMapping("/order")
    public Result<Void> updatePlatformCategorySort(@RequestBody PlatformCategorySortDTO platformCategorySortDto) {
        categoryService.updatePlatformCategorySort(platformCategorySortDto);
        return Result.ok();
    }

    /**
     * 类目删除
     *
     * @param id 类目id
     * @return Void
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteCategory(@PathVariable(name = "id") Long id) {
        categoryService.deleteCategory(id);
        return Result.ok();
    }

    /**
     * 获取类目信息 By page
     *
     * @param page 分页对象
     * @return 符合条件得类目信息
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/list")
    public Result<IPage<CategoryVO>> getCategoryList(Page<?> page) {
        IPage<CategoryVO> categoryList = categoryService.getCategoryList(page);
        return Result.ok(categoryList);
    }


    /**
     * 获取指定level 等级类目
     *
     * @param categoryParam 查询条件
     * @return PlatformCategory
     */
    @PreAuthorize("permitAll()")
    @GetMapping("list/level")
    public Result<IPage<PlatformCategory>> getCategoryListByLevel(CategoryParam categoryParam) {
        IPage<PlatformCategory> categoryVoPage = categoryService.getCategoryListByLevel(categoryParam);
        return Result.ok(categoryVoPage);
    }

    /**
     * 根据一级类目id获取三级类目id
     *
     * @param platformCategoryId 一级类目id
     * @return 三级类目ids
     */
    @PreAuthorize("permitAll()")
    @GetMapping("get/three/level")
    public Result<List<Long>> getLevelCategoryList(@RequestParam(value = "platformCategoryId") Long platformCategoryId) {
        List<Long> levelCategoryList = categoryService.getLevelCategoryList(platformCategoryId);
        return Result.ok(levelCategoryList);
    }


    /**
     * by ids
     *
     * @param categoryRank 类目级别
     * @return List<PlatformCategory>
     */
    @GetMapping("by/ids")
    @PreAuthorize("permitAll()")
    public Result<List<PlatformCategory>> getPlatformCategoryInfoByIds(PlatformCategoryRankDTO categoryRank) {
        return Result.ok(categoryService.getCategoryInfoByIds(categoryRank));
    }

    /**
     * 根据平台一级分类查看一级分类下的商品数量
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/platformCategoryFirstIdWithProductNum")
    public Result<IPage<CategoryFirstIdWithNumVO>> getCategoryFirstIdWithNumVO(Page<PlatformCategory> page) {
        return Result.ok(
                categoryService.getCategoryFirstIdWithNumVO(page)
        );
    }

    /**
     * 获取商品信息 by categoryRank
     *
     * @param categoryRank 分类级别查询
     * @return Page<ApiProductVO>
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/by/platformCategoryId")
    public Result<Page<ApiProductVO>> getProductInfoByPlatformCategoryId(PlatformCategoryRankDTO categoryRank) {
        return Result.ok(categoryService.getProductInfoByPlatformCategoryId(categoryRank));
    }
}

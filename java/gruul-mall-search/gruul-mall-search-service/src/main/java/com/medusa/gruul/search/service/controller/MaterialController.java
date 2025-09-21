package com.medusa.gruul.search.service.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.search.service.es.entity.EsMaterialCategoryEntity;
import com.medusa.gruul.search.service.es.entity.EsMaterialEntity;
import com.medusa.gruul.search.service.model.dto.*;
import com.medusa.gruul.search.service.model.vo.MaterialSearchSuggestVO;
import com.medusa.gruul.search.service.service.MaterialService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 素材管理控制器
 *
 * @author 张治保
 * @since 2023/9/22
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/search/material")
@PreAuthorize("""
                @S.matcher()
                .any(@S.ROLES,@S.PLATFORM_ADMIN,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN)
                .or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS,'material:management'))
                .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'material:management'))
                .match()
        """)
@Slf4j
public class MaterialController {

    private final MaterialService materialService;

    /**
     * 创建分类
     *
     * @param category 分类信息
     */
    @Log("创建分类")
    @PostMapping("/category")
    public Result<Void> category(@ModelAttribute @Valid MaterialCategoryDTO category) {
        materialService.category(ISecurity.userMust().getShopId(), category);
        return Result.ok();
    }

    /**
     * 删除分类
     *
     * @param categoryId 分类id
     */
    @Log("删除分类")
    @DeleteMapping("/category/{categoryId}")
    public Result<Void> deleteCategory(@PathVariable("categoryId") String categoryId) {
        materialService.deleteCategory(ISecurity.userMust().getShopId(), categoryId);
        return Result.ok();
    }

    /**
     * 修改分类名称
     *
     * @param categoryId 分类id
     * @param name       分类名称参数
     */
    @Log("修改分类名称")
    @PutMapping("/category/{categoryId}")
    public Result<Void> updateCategoryName(@PathVariable("categoryId") String categoryId, @RequestBody @Valid MaterialCategoryNameDTO name) {
        name.setId(categoryId);
        materialService.updateCategoryName(ISecurity.userMust().getShopId(), name);
        return Result.ok();
    }

    /**
     * 根据上级分类 id 查询子分类信息
     */
    @Log("根据上级分类 id 查询子分类信息")
    @GetMapping("/category/children")
    @PreAuthorize("@S.matcher().any(@S.ROLES,@S.PLATFORM_ADMIN,@S.PLATFORM_CUSTOM_ADMIN,@S.SHOP_ADMIN,@S.SHOP_CUSTOM_ADMIN,@S.R.SUPPLIER_ADMIN).match()")
    public Result<List<EsMaterialCategoryEntity>> getChildrenCategory(@RequestParam(value = "parentId", required = false) String parentId) {
        return Result.ok(
                materialService.getChildrenCategory(ISecurity.userMust().getShopId(), parentId)
        );
    }


    /**
     * 上传素材
     */
    @Log("上传素材")
    @PostMapping("/upload")
    public Result<String> materialUpload(@ModelAttribute @Valid MaterialDTO material) {
        materialService.material(ISecurity.userMust().getShopId(), material);
        return Result.ok();
    }

    /**
     * 获取素材推荐检索建议
     *
     * @param suggest 检索建议参数
     * @return 检索建议结果
     */
    @Log("获取素材推荐检索建议")
    @PostMapping("/suggest")
    @PreAuthorize("@S.matcher().any(@S.ROLES,@S.PLATFORM_ADMIN,@S.PLATFORM_CUSTOM_ADMIN,@S.SHOP_ADMIN,@S.SHOP_CUSTOM_ADMIN,@S.R.SUPPLIER_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN).match()")
    public Result<MaterialSearchSuggestVO> materialSuggest(@ModelAttribute MaterialSearchSuggestDTO suggest) {
        return Result.ok(
                materialService.materialSuggest(ISecurity.userMust().getShopId(), suggest.getCategoryId())
        );
    }

    /**
     * 素材分页查询
     *
     * @param param 检索参数
     * @return 检索结果
     */
    @Log("素材分页查询")
    @PostMapping
    @PreAuthorize("@S.matcher().any(@S.ROLES,@S.PLATFORM_ADMIN,@S.PLATFORM_CUSTOM_ADMIN,@S.SHOP_ADMIN,"
            + "@S.SHOP_CUSTOM_ADMIN,@S.R.SUPPLIER_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN).match()")
    public Result<IPage<EsMaterialEntity>> materialSearch(@RequestBody @Valid MaterialSearchParamDTO param) {
        return Result.ok(
                materialService.materialSearch(ISecurity.userMust().getShopId(), param)
        );
    }

    /**
     * 批量更换素材分类
     *
     * @param materialCategoryChange 素材分类更换参数
     * @return void
     */
    @Log("批量更换素材分类")
    @PutMapping("/move/to/category")
    public Result<Void> updateMaterialCategory(@RequestBody @Valid MaterialCategoryChangeDTO materialCategoryChange) {
        materialService.updateMaterialCategory(ISecurity.userMust().getShopId(), materialCategoryChange);
        return Result.ok();
    }

    /**
     * 批量删除素材
     */
    @Log("批量删除素材")
    @DeleteMapping
    public Result<Void> deleteMaterial(@RequestBody @Valid @Size(min = 1) Set<String> materialIds) {
        materialService.deleteMaterial(ISecurity.userMust().getShopId(), materialIds);
        return Result.ok();
    }

    /**
     * 修改素材名称
     */
    @Log("修改素材名称")
    @PutMapping("/{materialId}")
    public Result<Void> updateMaterialName(@PathVariable("materialId") String materialId, MaterialNameDTO name) {
        name.setId(materialId);
        materialService.updateMaterialName(ISecurity.userMust().getShopId(), name);
        return Result.ok();
    }

}

package com.medusa.gruul.addon.platform.model.dto;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.RegexPool;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.medusa.gruul.addon.platform.mp.entity.PlatformCategory;
import com.medusa.gruul.addon.platform.mp.service.IPlatformCategoryService;
import com.medusa.gruul.common.custom.aggregation.classify.enums.CategoryLevel;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Description: 类目DTO
 * @Author: xiaoq
 * @Date : 2022-04-18 14:29
 */
@Getter
@Setter
@ToString
public class CategoryDTO {

    private Long id;

    /**
     * 上机分类的编号：0表示一级分类
     */
    @NotNull
    private Long parentId;

    /**
     * 分类名称
     */
    @NotNull
    @Size(min = 1)
    private List<PlatformCategoryNameImgDTO> nameImgs;

    /**
     * 分类级别：0->1级；1->2级 ; 2>3级
     */
    @NotNull
    private CategoryLevel level;

    private List<PlatformCategory.Ad> ads;


    /**
     * 修改编辑分类
     */
    public void update(IPlatformCategoryService categoryService, Long categoryId) {
        PlatformCategoryNameImgDTO categoryNameImg = this.getNameImgs().iterator().next();
        String name = categoryNameImg.getName();
        boolean exists = categoryService.lambdaQuery()
                .eq(PlatformCategory::getName, name)
                .in(PlatformCategory::getParentId, Lists.newArrayList(getParentId(),getId()))
                .eq(PlatformCategory::getDeleted, 0)
                .ne(PlatformCategory::getId, categoryId).exists();

        if (exists) {
            throw new GlobalException(SystemCode.DATA_UPDATE_FAILED_CODE, "同级分类下,已存在同名分类");
        }
        exists=categoryService.lambdaQuery()
                .eq(PlatformCategory::getId, getParentId())
                .in(PlatformCategory::getName,getNameImgs().stream().map(PlatformCategoryNameImgDTO::getName).collect(Collectors.toList()))
                .exists();
        if (exists){
            throw new GlobalException(SystemCode.DATA_ADD_FAILED_CODE, "添加分类不可与上级分类同名");
        }
        PlatformCategory category = categoryService.getById(categoryId);
        if (category == null) {
            throw new GlobalException(SystemCode.DATA_NOT_EXIST_CODE, "未查到对应数据");
        }
        category.setParentId(this.getParentId())
                .setName(name)
                .setAds(getAds())
                .setDeductionRatio(categoryNameImg.getDeductionRatio())
                .setCategoryImg(categoryNameImg.getCategoryImg());
        boolean success = categoryService.updateById(category);
        if (!success) {
            throw new GlobalException(SystemCode.DATA_UPDATE_FAILED_CODE, "更新数据失败");
        }
    }


    /**
     * 新增分类
     */
    public void save(IPlatformCategoryService categoryService) {
        boolean exists = categoryService.lambdaQuery()
                .in(PlatformCategory::getName, getNameImgs().stream().map(PlatformCategoryNameImgDTO::getName).collect(Collectors.toList()))
                .eq(PlatformCategory::getParentId, getParentId())
                .exists();
        if (exists) {
            throw new GlobalException(SystemCode.DATA_ADD_FAILED_CODE, "同级分类下,已存在同名分类");
        }
        exists=categoryService.lambdaQuery()
                .eq(PlatformCategory::getId, getParentId())
                .in(PlatformCategory::getName,getNameImgs().stream().map(PlatformCategoryNameImgDTO::getName).collect(Collectors.toList()))
                .exists();
        if (exists){
            throw new GlobalException(SystemCode.DATA_ADD_FAILED_CODE, "添加分类不可与上级分类同名");
        }
        Long parentId = this.getParentId();
        Long count = categoryService.lambdaQuery()
                .eq(PlatformCategory::getParentId, parentId)
                .count();
        final AtomicInteger sort = new AtomicInteger(count == null ? 0 : count.intValue() + 1);
        /*
         * 批量保存
         */
        boolean success = categoryService.saveBatch(
                this.getNameImgs().stream().map(
                        nameImg -> {
                            PlatformCategory category = new PlatformCategory()
                                    .setParentId(parentId)
                                    .setAds(getAds())
                                    .setName(nameImg.getName())
                                    .setSort(sort.get())
                                    .setLevel(this.getLevel())
                                    .setDeductionRatio(CategoryLevel.LEVEL_2 == this.level ? nameImg.getDeductionRatio() : null)
                                    .setCategoryImg(CategoryLevel.LEVEL_1 != this.getLevel() ? nameImg.getCategoryImg() : null);
                            sort.incrementAndGet();
                            return category;
                        }
                ).collect(Collectors.toList())
        );
        if (!success) {
            throw new GlobalException(SystemCode.DATA_ADD_FAILED_CODE, "保存数据失败");
        }
    }


    /**
     * 保存或更新
     */
    public void saveOrUpdate(IPlatformCategoryService categoryService, Long id) {
        boolean isEdit = id != null;
        if (isEdit) {
            this.update(categoryService, id);
        } else {
            this.save(categoryService);
        }


    }


    /**
     * 检查参数
     */
    public void paramCheck() {
        switch (level) {
            case LEVEL_1:
                this.setParentId(0L);
                break;
            case LEVEL_2:
                if (CollectionUtil.isNotEmpty(getAds())) {
                    throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "非一级类目不可添加广告");
                }
                List<PlatformCategoryNameImgDTO> deductionRatioList = getNameImgs();
                deductionRatioList.forEach(
                        ratio -> {
                            Long deductionRatio = ratio.getDeductionRatio();
                            if (deductionRatio == null) {
                                throw new GlobalException("二级类目倍率不可为空");
                            }
                            String categoryImg = ratio.getCategoryImg();
                            if (StrUtil.isEmpty(categoryImg)) {
                                throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "categoryImg不能为空");
                            }
                            if (!ReUtil.isMatch(RegexPool.URL_HTTP, categoryImg)) {
                                throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "不符合格式要求");
                            }
                        }
                );
                break;
            case LEVEL_3:
                if (CollectionUtil.isNotEmpty(getAds())) {
                    throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "非一级类目不可添加广告");
                }
                List<PlatformCategoryNameImgDTO> nameImgs = getNameImgs();
                nameImgs.forEach(
                        nameImg -> {
                            String categoryImg = nameImg.getCategoryImg();
                            if (StrUtil.isEmpty(categoryImg)) {
                                throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "categoryImg不能为空");
                            }
                            if (!ReUtil.isMatch(RegexPool.URL_HTTP, categoryImg)) {
                                throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "不符合格式要求");
                            }
                        }
                );
            default:
                break;
        }
    }

    public PlatformCategory coverCategory() {
        PlatformCategory category = new PlatformCategory();
        BeanUtil.copyProperties(this, category);
        return category;
    }
}

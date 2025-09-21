package com.medusa.gruul.goods.service.model.dto;

import cn.hutool.core.lang.RegexPool;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.custom.aggregation.classify.enums.CategoryLevel;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.web.valid.group.Create;
import com.medusa.gruul.common.web.valid.group.Update;
import com.medusa.gruul.goods.api.entity.ProductCategory;
import com.medusa.gruul.goods.api.model.enums.GoodsError;
import com.medusa.gruul.goods.service.mp.service.IProductCategoryService;
import com.medusa.gruul.goods.service.util.GoodsUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保 date 2022/4/22
 */
@Getter
@Setter
@ToString
public class ProductCategoryDTO {

    /**
     * 上级分类的编号：0表示一级分类
     */
    @NotNull(groups = {Create.class, Update.class})
    @Min(value = 0, groups = {Create.class, Update.class})
    private Long parentId;
    /**
     * 分类名称
     */
    @NotNull(groups = {Create.class, Update.class})
    @Size(min = 1, groups = Create.class)
    @Size(min = 1, max = 1, groups = Update.class)
    @Valid
    private List<CategoryNameImgDTO> nameImages;
    /**
     * 分类级别
     */
    @NotNull(groups = Create.class)
    private CategoryLevel level;

    /**
     * 修改编辑分类
     */
    public void update(IProductCategoryService productCategoryService, Long categoryId) {
        CategoryNameImgDTO categoryNameImg = this.getNameImages().iterator().next();
        String name = categoryNameImg.getName();

        boolean exists = productCategoryService.lambdaQuery()
                .eq(ProductCategory::getName, name)
                .eq(ProductCategory::getParentId, getParentId())
                .ne(ProductCategory::getId, categoryId).exists();
        GoodsError.SIBLING_CLASSIFY_NAME_REPETITION.trueThrow(exists);
        exists = productCategoryService.lambdaQuery()
                .eq(ProductCategory::getName, name)
                .eq(ProductCategory::getId, getParentId())
                .ne(ProductCategory::getId, categoryId).exists();
        GoodsError.CLASSIFY_NAME_REPETITION.trueThrow(exists);
        ProductCategory category = productCategoryService.getById(categoryId);
        GoodsError.CLASSIFY_DATA_NOT_EXIST.trueThrow(category == null);
        assert category != null;
        category.setParentId(this.getParentId())
                .setName(name)
                .setCategoryImg(categoryNameImg.getCategoryImg());
        RedisUtil.doubleDeletion(() -> {
            boolean success = productCategoryService.updateById(category);
            GoodsError.CLASSIFY_UPDATE_FAIL.falseThrow(success);
        }, GoodsUtil.productCategoryCacheKey(ISystem.shopIdMust(), categoryId));


    }

    /**
     * 新增分类
     */
    public void save(IProductCategoryService productCategoryService) {

        boolean exists = productCategoryService.lambdaQuery()
                .in(ProductCategory::getName,
                        getNameImages().stream().map(CategoryNameImgDTO::getName).collect(Collectors.toList())
                ).eq(ProductCategory::getParentId, getParentId())
                .exists();
        GoodsError.SIBLING_CLASSIFY_NAME_REPETITION.trueThrow(exists);

        exists = productCategoryService.lambdaQuery()
                .in(ProductCategory::getName,
                        getNameImages().stream().map(CategoryNameImgDTO::getName).collect(Collectors.toList())
                ).eq(ProductCategory::getId, getParentId())
                .exists();
        GoodsError.CLASSIFY_NAME_REPETITION.trueThrow(exists);

        Long parentId = this.getParentId();
        Long count = productCategoryService.lambdaQuery()
                .eq(ProductCategory::getParentId, parentId)
                .count();
        final AtomicInteger sort = new AtomicInteger(count == null ? 0 : count.intValue());
        /*
         * 批量保存
         */
        boolean success = productCategoryService.saveBatch(
                this.getNameImages().stream().map(
                        nameImg -> {
                            ProductCategory category = new ProductCategory()
                                    .setParentId(parentId)
                                    .setName(nameImg.getName())
                                    .setSort(sort.get())
                                    .setLevel(this.getLevel())
                                    .setCategoryImg(
                                            CategoryLevel.LEVEL_1 != this.getLevel() ? nameImg.getCategoryImg() : null);
                            sort.incrementAndGet();
                            return category;
                        }
                ).collect(Collectors.toList())
        );
        GoodsError.CLASSIFY_SAVE_FAIL.falseThrow(success);
    }

    /**
     * 检查参数
     */
    public void paramCheck() {
        switch (getLevel()) {
            case LEVEL_1:
                this.setParentId(0L);
                break;
            case LEVEL_2:
            case LEVEL_3:
                List<CategoryNameImgDTO> nameImgs = getNameImages();
                Set<String> categoryNameSet = new HashSet<>();
                nameImgs.forEach(
                        nameImg -> {
                            String categoryImg = nameImg.getCategoryImg();
                            GoodsError.CATEGORY_IMG_NOT_EXIST.trueThrow(StrUtil.isEmpty(categoryImg));
                            if (!ReUtil.isMatch(RegexPool.URL_HTTP, categoryImg)) {
                                throw GoodsError.CATEGORY_BMP_FAIL.exception();
                            }
                            boolean flag = categoryNameSet.add(nameImg.getName());
                            if (!flag) {
                                throw GoodsError.SET_SIBLING_CLASSIFY_NAME_REPETITION.exception(nameImg.getName());
                            }

                        }
                );

            default:
        }
    }
}

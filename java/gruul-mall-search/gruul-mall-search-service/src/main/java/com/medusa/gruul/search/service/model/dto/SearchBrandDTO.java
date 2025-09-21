package com.medusa.gruul.search.service.model.dto;

import com.medusa.gruul.common.web.valid.group.Update;
import com.medusa.gruul.search.api.enums.BrandStatus;
import com.medusa.gruul.search.service.mp.entity.SearchBrand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SearchBrandDTO {

    /**
     * 品牌id
     */
    @NotNull(groups = {Update.class},message = "品牌id不能为空")
    private Long id;


    /**
     * 品牌名称
     */
    @Size(max = 30)
    @NotBlank(message = "品牌名称不能为空")
    private String brandName;

    /**
     * 品牌描述
     */
    private String brandDesc;

    /**
     * 品牌logo
     */
    @NotBlank(message = "品牌logo不能为空")
    private String brandLogo;


    /**
     * 检索首字母
     */
    @Size(max = 1)
    @NotBlank(message = "检索首字母不能为空")
    @Pattern(regexp = "^[A-Z]+$")
    private String searchInitials;

    /**
     * 上级类目/上级分类
     */
    @NotNull
    private Long parentCategoryId;

    /**
     * 关注人数
     */
    private Integer followers;

    /**
     * 状态(默认上架，0--下架 1--上架)
     */
    private BrandStatus status;

    /**
     * 排序
     */
    @NotNull
    private Integer sort;

    public SearchBrand saveBrand(Boolean update) {
        SearchBrand brand = new SearchBrand();
        brand.setBrandName(brandName)
                .setBrandDesc(brandDesc)
                .setBrandLogo(brandLogo)
                .setSearchInitials(searchInitials)
                .setParentCategoryId(parentCategoryId)
                .setStatus(update ? status : BrandStatus.SELL_ON)
                .setFollowers(followers)
                .setSort(sort)
                .setId(id);
        return brand;
    }
}

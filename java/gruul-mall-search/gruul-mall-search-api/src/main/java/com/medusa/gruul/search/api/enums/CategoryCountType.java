package com.medusa.gruul.search.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2024/3/18
 */
@RequiredArgsConstructor
@Getter
public enum CategoryCountType {
    /**
     * 平台分类商品数统计
     */
    PLATFORM("platformCategoryFirstId", "platformCategorySecondId", "platformCategoryThirdId","salesVolume"),
    /**
     * 店铺分类商品统计
     */
    SHOP("categoryFirstId", "categorySecondId", "categoryThirdId","salesVolume");
    
    /**
     * 一级分类字段名称
     */
    private final String first;

    /**
     * 二级分类字段名称
     */
    private final String second;

    /**
     * 三级分类字段名称
     */
    private final String third;

    private final String salesVolume;
}

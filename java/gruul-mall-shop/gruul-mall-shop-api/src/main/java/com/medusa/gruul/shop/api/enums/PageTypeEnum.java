package com.medusa.gruul.shop.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 页面类型枚举
 *
 * @author An.Yan
 */
@Getter
@RequiredArgsConstructor
public enum PageTypeEnum {
    /**
     * 商城首页(推荐)
     */
    RECOMMENDED_MALL_HOME_PAGE(1, true),

    /**
     * 商城首页(同城)
     */
    SAME_CITY_MALL_HOME_PAGE(2, true),

    /**
     * 商品分类
     */
    PRODUCT_CATEGORY_PAGE(3, true),

    /**
     * 底部导航
     */
    BOTTOM_NAVIGATION_PAGE(4, true),

    /**
     * 个人中心
     */
    PERSONAL_CENTER_PAGE(5, true),

    /**
     * 自定义页面
     */
    CUSTOMIZED_PAGE(6, true),

    /**
     * 店铺首页
     */
    SHOP_HOME_PAGE(7, false),

    /**
     * 店铺底部导航
     */
    SHOP_BOTTOM_NAVIGATION_PAGE(8, false),

    /**
     * 店铺分类
     */
    SHOP_CATEGORY_PAGE(9, false),

    /**
     * 自定义页面
     */
    SHOP_CUSTOMIZED_PAGE(10, false),
    ;

    @EnumValue
    private final Integer value;

    /**
     * 是否是平台页面
     */
    private final boolean platform;
}

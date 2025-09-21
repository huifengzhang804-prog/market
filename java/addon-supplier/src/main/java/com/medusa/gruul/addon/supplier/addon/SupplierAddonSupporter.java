package com.medusa.gruul.addon.supplier.addon;

import com.medusa.gruul.common.addon.supporter.annotation.AddonMethod;
import com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter;
import com.medusa.gruul.global.model.constant.Services;
import com.medusa.gruul.goods.api.model.CategoryLevel;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.model.vo.CategoryLevelName;

import java.util.Set;

/**
 * 供应商商品服务提供插件支持器
 *
 * @author xiaoq
 * @Description SupplierGoodsAddonSupporter.java
 * @date 2023-07-19 09:18
 */
@AddonSupporter(service = Services.GRUUL_MALL_GOODS, id = "goodsAddonSupporter")
public interface SupplierAddonSupporter {

    /**
     * 获取签约类目ids
     *
     * @param shopId 店铺id
     * @return 签约二级类目ids
     */
    @AddonMethod(returnType = Set.class)
    Set<Long> getSigningCategoryIds(Long shopId);

    /**
     * 获取签约类目 自定义扣除百分比
     *
     * @param two    签约二级类目
     * @param shopId 店铺id
     * @return 扣除百分比
     */
    @AddonMethod(returnType = Long.class)
    Long getCustomDeductionRatio(Long two, Long shopId);

    /**
     * 获取平台类目级别名称
     *
     * @param platformCategory 平台类目级别id
     * @return 平台类目级别名称
     */
    @AddonMethod(returnType = CategoryLevelName.class)
    CategoryLevelName getPlatformCategoryLevelName(CategoryLevel platformCategory);

    /**
     * 获取商品状态
     *
     * @param supplierProductStatus 供应商商品状态
     * @return ProductStatus
     */
    @AddonMethod(returnType = ProductStatus.class)
    ProductStatus getProductStatus(ProductStatus supplierProductStatus);

}

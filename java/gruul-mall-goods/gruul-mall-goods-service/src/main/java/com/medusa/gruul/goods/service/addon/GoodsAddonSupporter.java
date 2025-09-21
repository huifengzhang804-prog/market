package com.medusa.gruul.goods.service.addon;

import com.medusa.gruul.common.addon.supporter.annotation.AddonMethod;
import com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.goods.api.constant.GoodsConstant;
import com.medusa.gruul.goods.api.model.CategoryLevel;
import com.medusa.gruul.goods.api.model.dto.ProductDTO;
import com.medusa.gruul.goods.api.model.dto.ProductStatusChangeDTO;
import com.medusa.gruul.goods.api.model.enums.DiscountType;
import com.medusa.gruul.goods.api.model.enums.EarningType;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.model.param.EarningParam;
import com.medusa.gruul.goods.api.model.vo.*;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 商品服务提供插件支持器
 *
 * @author xiaoq
 * @Description GoodsAddonSupporter.java
 * @date 2022-09-17 09:30
 */
@AddonSupporter(id = GoodsConstant.ADDON_SUPPORT_ID)
public interface GoodsAddonSupporter {

    /**
     * 获取商品状态 addonSupporter
     *
     * @param status 商品状态
     * @return list<商品状态>
     * <p>
     * 插件实现服务 addon-platform
     * {@link com.medusa.gruul.addon.platform.addon.impl.PlatformAddonProviderImpl#getProductStatus}
     */
    @AddonMethod(returnType = ProductStatus.class)
    ProductStatus getProductStatus(ProductStatus status);

    /**
     * 根据三级类目id获取对应的一级、二级类目id
     *
     * @param platformCategoryIdSet 三级类目id
     * @return List<三级类目>
     * 插件实现服务 addon-platform
     * {@link com.medusa.gruul.addon.platform.addon.impl.PlatformAddonProviderImpl#getPlatformCategoryVoByLevel3Id}
     */
    @AddonMethod(returnType = PlatformCategoryVo.class)
    PlatformCategoryVo getPlatformCategoryVoByLevel3Id(Set<Long> platformCategoryIdSet);

    /**
     * 获取平台店铺签约类目自定义扣率
     *
     * @param platformTwoCategory 平台二级类目
     * @param shopId              店铺id
     * @return 签约类目自定义扣率
     * <p>
     * 插件实现服务 addon-platform
     * {@link com.medusa.gruul.addon.platform.addon.impl.PlatformAddonProviderImpl#getCustomDeductionRatio}
     */
    @AddonMethod(returnType = Long.class)
    Long getCustomDeductionRatio(Long platformTwoCategory, Long shopId);


    /**
     * 采购商品发布
     *
     * @param productId  商品id
     * @param shopId     店铺id
     * @param supplierId 供应id
     *                   <p>
     *                   插件实现服务 addon-supplier
     *                   {@link
     *                   com.medusa.gruul.addon.supplier.addon.impl.AddonSupplierProviderImpl#purchaseProductIssue}
     */
    @AddonMethod(returnType = void.class)
    void purchaseProductIssue(Long productId, Long shopId, Long supplierId);

    /**
     * 同步供应商商品的状态
     *
     * @param param  商品key及状态描述
     * @param status 目标状态
     */
    @AddonMethod(returnType = void.class)
    void syncSupplierProduct(ProductStatusChangeDTO param, ProductStatus status);

    /**
     * 统计所有供应商商品数据
     *
     * @return {@link Map}
     * <p>
     * 插件实现服务 addon-supplier
     * {@link com.medusa.gruul.addon.supplier.addon.impl.AddonSupplierProviderImpl#countProductsOfAllSupplier}
     */
    @AddonMethod(returnType = Map.class)
    Map<String, Integer> countProductsOfAllSupplier();

    /**
     * 代销商品获取
     *
     * @param shopProductKeys 代销商品 唯一sky
     * @return List<ProductDTO>
     * <p>
     * 插件实现服务 addon-supplier
     * {@link com.medusa.gruul.addon.supplier.addon.impl.AddonSupplierProviderImpl#getSupplierGoods}
     */
    @AddonMethod(returnType = List.class)
    List<ProductDTO> getSupplierGoods(Set<ShopProductKey> shopProductKeys);

    /**
     * 获取平台类目级别名称
     *
     * @param platformCategory 平台类目级别id
     * @return 平台类目级别名称
     * 插件实现服务 addon-platform
     * {@link com.medusa.gruul.addon.platform.addon.impl.PlatformAddonProviderImpl#getPlatformCategoryLevelName}
     */
    @AddonMethod(returnType = CategoryLevelName.class)
    CategoryLevelName getPlatformCategoryLevelName(CategoryLevel platformCategory);

    /**
     * 获取砍价商品sku详情
     *
     * @param type       路由请求的 活动类型
     * @param activityId 活动 id
     * @param userId     用户 id
     * @param key        商品 key
     * @return 活动信息 详情
     */
    @AddonMethod(returnType = ActivityDetailVO.class, arg1Filter = true)
    ActivityDetailVO activity(OrderType type, Long activityId, @Nullable Long userId, ShopProductSkuKey key);

    /**
     * 折扣信息 优惠券、满减
     *
     * @param type   折扣类型
     * @param userId 用户 id
     * @param key    商品 key
     * @param amount 商品金额
     * @return 当前折扣信息
     */
    @AddonMethod(returnType = ProductDiscountVO.class, arg1Filter = true)
    ProductDiscountVO discount(DiscountType type, @Nullable Long userId, ShopProductKey key, Long amount);

    /**
     * 分销、返利预计赚
     *
     * @param type  预计赚类型
     * @param param 预计赚请求参数
     * @return 预计赚金额
     */
    @AddonMethod(returnType = Long.class, arg1Filter = true)
    Long earning(EarningType type, EarningParam param);


    /**
     * 商品详情页套餐基本信息
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 套餐基本信息
     * {@link com.medusa.gruul.addon.matching.treasure.addon.impl.AddonMatchingTreasureProviderImpl#getSetMealBasicInfo}
     */
    List<SetMealBasicInfoVO> getSetMealBasicInfo(Long shopId, Long productId);

    /**
     * @param shopId
     * @return{@link com.medusa.gruul.addon.ic.addon.impl.IcAddonProviderImpl#queryShopIcStatus}
     */
    @AddonMethod(returnType = Boolean.class)
    Boolean queryShopIcStatus(Long shopId);

    /**
     * 获取店铺前3个优先级高的优惠券规则 search 服务 优先级：无门槛现金券 > 无门槛折扣券 > 满减券 > 满折券
     *
     * @param shopIds
     * @return
     * @link com.medusa.gruul.addon.coupon.addon.AddonCouponProvider#getCouponListForProduct
     */
    List<GoodsCouponVO> getCouponListForProduct(Set<Long> shopIds);
}

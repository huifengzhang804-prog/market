package com.medusa.gruul.addon.platform.addon;


import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.goods.api.model.CategoryLevel;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.model.vo.CategoryLevelName;
import com.medusa.gruul.goods.api.model.vo.PlatformCategoryVo;
import com.medusa.gruul.shop.api.model.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;

/**
 * @author xiaoq
 */

public interface PlatformAddonProvider {

    /**
     * com.medusa.gruul.goods.service.addon.GoodsAddonSupporter
     *
     * @param status 商品状态
     * @return 修改后商品状态
     */
    ProductStatus getProductStatus(ProductStatus status);

    /**
     * 根据三级类目id获取对应的一级、二级类目id
     *
     * @param platformCategoryIdSet 三级类目id
     * @return PlatformCategoryVo.java
     */
    PlatformCategoryVo getPlatformCategoryVoByLevel3Id(Set<Long> platformCategoryIdSet);


    /**
     * 保存店铺平台签约类目
     *
     * @param signingCategories 签约类目
     * @param shopId            店铺id
     * @return 是否成功
     */
    Boolean editPlatformShopSigningCategory(List<SigningCategoryDTO> signingCategories, Long shopId, ShopMode shopMode);


    /**
     * 根据平台二级类目获取之定义扣率
     *
     * @param platformTwoCategory 平台二级类目
     * @param shopId              店铺id
     * @return 自定义类目扣率
     */
    Long getCustomDeductionRatio(Long platformTwoCategory, Long shopId);


    /**
     * 获取店铺签约类目ids
     *
     * @param shopId 店铺id
     * @return 签约二级类目ids
     */
    Set<Long> getSigningCategoryIds(Long shopId);


    /**
     * 获取平台对应类目名称
     *
     * @param platformCategory 平台类目信息
     * @return {@link CategoryLevelName}
     */
    CategoryLevelName getPlatformCategoryLevelName(CategoryLevel platformCategory);

    /**
     * 根据业务类型 随机拉取平台创建的所有店铺模板
     *
     * @param param 模板参数
     * @return {@link PlatformShopDecorationTemplate} 店铺装修模板
     */
    PlatformShopDecorationTemplate platformTemplate(@NotNull @Valid DecorationCopyTemplateParamDTO param);


    /**
     * 根据装修页面参数复制平台装修页面
     *
     * @param param 页面参数
     * @return {@link DecorationCopyPageDTO} 装修页面数据
     */
    DecorationCopyPageDTO platformPage(@NotNull @Valid DecorationCopyPageParamDTO param);

    /**
     * 获取平台的名称
     * @return
     */
    String getPlatformName();
}

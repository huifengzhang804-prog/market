package com.medusa.gruul.shop.service.service.addon;

import com.medusa.gruul.common.addon.supporter.annotation.AddonMethod;
import com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.shop.api.model.dto.*;
import com.vividsolutions.jts.geom.Point;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 店铺插件供应者
 *
 * @author xiaoq
 * @since 2023-05-15 15:50
 */

@AddonSupporter(id = "shopSigningCategory")
public interface ShopAddonSupporter {

    /**
     * 编辑签约类目
     *
     * @param signingCategory 签约类目信息
     * @param shopId;
     * @return 是否成功
     * <p>
     * 插件实现服务 addon-platform
     * {@link com.medusa.gruul.addon.platform.addon.impl.PlatformAddonProviderImpl#editPlatformShopSigningCategory}
     */
    @AddonMethod(returnType = Boolean.class)
    Boolean editSingingCategory(List<SigningCategoryDTO> signingCategory, Long shopId, ShopMode shopMode);

    /**
     * 获取店铺的起送金额
     *
     * @param shopIds 店铺ids
     * @return 获取店铺的起送金额
     * todo 需要和所有价格统一 全部使用Long 类型 单位 豪
     * <p>
     * 插件实现服务 addon-intra-city-distribution
     * {@link
     * com.medusa.gruul.addon.distribution.addon.impl.IntraCityDistributionAddonProviderImpl#getShopInitialDeliveryCharge}
     */
    @AddonMethod(returnType = Map.class)
    Map<Long, BigDecimal> shopInitialDeliveryCharge(Set<Long> shopIds);


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
     * 获取店铺同城配送的运费信息
     *
     * @param shopIds
     * @return
     */
    Map<Long, ShopIcDistributeInfoDTO> getShopIcDistributeInfo(Set<Long> shopIds, Point point);
}

package com.medusa.gruul.shop.service.service.addon;

import com.medusa.gruul.common.addon.supporter.annotation.AddonMethod;
import com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter;
import com.medusa.gruul.search.api.model.SearchCouponVO;
import com.medusa.gruul.shop.api.addon.ShopAddonConstant;

import java.util.List;
import java.util.Set;

/**
 * 优惠券插件
 *
 * @author wufuzhong
 * @Date 2023-12-11
 */
@AddonSupporter(id = ShopAddonConstant.SHOP_COUPON_SUPPORT_ID)
public interface CouponAddonSupporter {

    /**
     * 获取店铺前3个优先级高的优惠券规则
     *
     * @param shopIds 店铺id
     * @return 优惠券规则
     *
     * 插件实现服务 addon-coupon {@link com.medusa.gruul.addon.coupon.addon.impl.AddonCouponProviderImpl#getShopCouponList}
     */
    @AddonMethod(returnType = List.class)
    List<SearchCouponVO> getShopCouponList(Set<Long> shopIds);
}

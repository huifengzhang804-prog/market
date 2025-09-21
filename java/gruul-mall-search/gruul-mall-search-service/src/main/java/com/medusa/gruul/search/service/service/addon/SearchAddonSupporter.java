package com.medusa.gruul.search.service.service.addon;

import com.medusa.gruul.common.addon.supporter.annotation.AddonMethod;
import com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter;
import com.medusa.gruul.search.api.addon.coupon.SearchAddonConstant;
import com.medusa.gruul.search.api.model.SearchCouponVO;

import java.util.List;
import java.util.Set;

/**
 * 优惠券插件
 *
 * @author wufuzhong
 * @Date 2023-12-07
 */
@AddonSupporter(id = SearchAddonConstant.SEARCH_COUPON_SUPPORT_ID)
public interface SearchAddonSupporter {

    /**
     * 获取店铺前3个优先级高的优惠券规则
     *
     * @param shopIds 店铺id
     * @return 优惠券规则
     *
     * 插件实现服务 addon-coupon {@link com.medusa.gruul.addon.coupon.addon.impl.AddonCouponProviderImpl#getCouponList}
     */
    @AddonMethod(returnType = List.class)
    List<SearchCouponVO> getCouponList(Set<Long> shopIds);
}

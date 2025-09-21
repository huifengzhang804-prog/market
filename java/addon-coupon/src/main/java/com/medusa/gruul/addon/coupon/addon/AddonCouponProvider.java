package com.medusa.gruul.addon.coupon.addon;

import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.goods.api.model.vo.GoodsCouponVO;
import com.medusa.gruul.goods.api.model.vo.ProductDiscountVO;
import com.medusa.gruul.order.api.addon.coupon.CouponResponse;
import com.medusa.gruul.order.api.addon.coupon.OrderCouponParam;
import com.medusa.gruul.search.api.model.SearchCouponVO;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * @author 张治保 date 2022/11/4
 */
public interface AddonCouponProvider {

    String FILTER = "COUPON";

    /**
     * 使用优惠券
     *
     * @param orderCoupon 订单优惠券参数
     * @return 计算优惠后的结果
     */
    CouponResponse useCoupon(OrderCouponParam orderCoupon);

    /**
     * 获取店铺前3个优先级高的优惠券规则 search 服务 优先级：无门槛现金券 > 无门槛折扣券 > 满减券 > 满折券
     *
     * @param shopIds 店铺id
     * @return List<SearchCouponVO>
     */
    List<SearchCouponVO> getCouponList(Set<Long> shopIds);

    List<GoodsCouponVO> getCouponListForProduct(Set<Long> shopIds);

    /**
     * 获取店铺前3个优先级高的优惠券规则 shop 服务 优先级：无门槛现金券 > 无门槛折扣券 > 满减券 > 满折券
     *
     * @param shopIds 店铺id
     * @return List<SearchCouponVO>
     */
    List<SearchCouponVO> getShopCouponList(Set<Long> shopIds);


    /**
     * 商品详情优惠券优惠
     *
     * @param userId 用户id
     * @param key    商品 key
     * @param amount 商品金额
     * @return 商品折扣信息
     */
    ProductDiscountVO discount(@Nullable Long userId, ShopProductKey key, Long amount);
}

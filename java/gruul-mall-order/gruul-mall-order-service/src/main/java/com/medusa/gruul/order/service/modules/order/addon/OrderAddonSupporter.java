package com.medusa.gruul.order.service.modules.order.addon;

import com.medusa.gruul.common.addon.supporter.annotation.AddonMethod;
import com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.order.api.addon.OrderAddonConstant;
import com.medusa.gruul.order.api.addon.activity.ActivityParam;
import com.medusa.gruul.order.api.addon.activity.ActivityResp;
import com.medusa.gruul.order.api.addon.coupon.CouponResponse;
import com.medusa.gruul.order.api.addon.coupon.OrderCouponParam;
import com.medusa.gruul.order.api.addon.fullreduction.FullReductionResponse;
import com.medusa.gruul.order.api.addon.fullreduction.OrderFullReductionParam;
import com.medusa.gruul.order.api.addon.rebate.RebatePayParam;
import com.medusa.gruul.order.api.addon.rebate.RebatePayResponse;

/**
 * @author 张治保
 * date 2022/11/4
 */
@AddonSupporter(id = OrderAddonConstant.ORDER_DISCOUNT_SUPPORT_ID)
public interface OrderAddonSupporter {

    /**
     * 使用优惠券
     *
     * @param couponParam 优惠券参数
     * @return 订单折扣项列表
     * 插件实现服务  addon-coupon {@link com.medusa.gruul.addon.coupon.addon.impl.AddonCouponProviderImpl#useCoupon}
     */
    @AddonMethod(returnType = CouponResponse.class)
    CouponResponse coupon(OrderCouponParam couponParam);

    /**
     * 订单活动预计算
     *
     * @param activityType filter 用于过滤活动插件
     * @param param        订单活动参数
     * @return 拼团活动信息
     */
    @AddonMethod(returnType = ActivityResp.class, arg1Filter = true)
    ActivityResp activityBudget(OrderType activityType, ActivityParam param);


    /**
     * 订单活动
     *
     * @param activityType filter 用于过滤活动插件
     * @param param        订单活动参数
     * @return 拼团活动信息
     * 插件实现服务 *多元* 活动
     */
    @AddonMethod(returnType = ActivityResp.class, arg1Filter = true)
    ActivityResp activity(OrderType activityType, ActivityParam param);

    /**
     * 获取满减可用的优惠列表
     *
     * @param orderFullReductionParam 订单满减参数
     * @return 订单折扣项列表
     * 插件实现服务  addon-full-reduction {@link com.medusa.gruul.addon.full.reduction.addon.impl.AddonFullReductionProviderImpl#getFullReductionsOrderDiscount}
     */
    @AddonMethod(returnType = FullReductionResponse.class)
    FullReductionResponse fullReduction(OrderFullReductionParam orderFullReductionParam);

    /**
     * 获取返利支付折扣数据
     *
     * @param payParam 返利支付参数
     * @return 返利支付响应数据
     * 插件实现服务  addon-rebate {@link com.medusa.gruul.addon.rebate.addon.impl.AddonRebateProviderImpl#rebateDiscount}
     */
    RebatePayResponse rebateDiscount(RebatePayParam payParam);


}

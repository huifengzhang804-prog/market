package com.medusa.gruul.addon.bargain.addon.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import com.google.common.collect.Lists;
import com.medusa.gruul.addon.bargain.addon.AddonBargainProvider;
import com.medusa.gruul.addon.bargain.model.BargainErrorCode;
import com.medusa.gruul.addon.bargain.model.enums.ActivityStatus;
import com.medusa.gruul.addon.bargain.model.enums.BargainStatus;
import com.medusa.gruul.addon.bargain.mp.entity.Bargain;
import com.medusa.gruul.addon.bargain.mp.entity.BargainOrder;
import com.medusa.gruul.addon.bargain.mp.entity.BargainProduct;
import com.medusa.gruul.addon.bargain.mp.service.IBargainHelpPeopleService;
import com.medusa.gruul.addon.bargain.mp.service.IBargainOrderService;
import com.medusa.gruul.addon.bargain.mp.service.IBargainProductService;
import com.medusa.gruul.addon.bargain.mp.service.IBargainService;
import com.medusa.gruul.addon.bargain.service.BargainConsumerService;
import com.medusa.gruul.addon.bargain.vo.BargainProductDetailVO;
import com.medusa.gruul.addon.bargain.vo.BargainSponsorProductSkuVO;
import com.medusa.gruul.addon.bargain.vo.BargainSponsorProductVO;
import com.medusa.gruul.common.addon.provider.AddonProvider;
import com.medusa.gruul.common.addon.provider.AddonProviders;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.model.base.StackableDiscount;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.global.model.constant.Services;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.o.RangeDateTime;
import com.medusa.gruul.goods.api.constant.GoodsConstant;
import com.medusa.gruul.goods.api.model.vo.ActivityDetailVO;
import com.medusa.gruul.order.api.addon.OrderAddonConstant;
import com.medusa.gruul.order.api.addon.activity.ActivityParam;
import com.medusa.gruul.order.api.addon.activity.ActivityResp;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


/**
 * @author wudi
 */
@Service
@DubboService
@AddonProviders
@RequiredArgsConstructor
public class AddonBargainProviderImpl implements AddonBargainProvider {

    private final IBargainService bargainService;
    private final IBargainOrderService bargainOrderService;
    private final IBargainProductService bargainProductService;

    private final IBargainHelpPeopleService bargainHelpPeopleService;
    private final BargainConsumerService bargainConsumerService;


    @Override
    @AddonProvider(filter = "BARGAIN", service = Services.GRUUL_MALL_ORDER, supporterId = OrderAddonConstant.ORDER_DISCOUNT_SUPPORT_ID, methodName = "activityBudget")
    public ActivityResp bargainBudget(ActivityParam param) {
        Long userId = param.getUserId();
        Long activityId = param.getActivityId();
        ShopProductSkuKey shopProductSkuKey = param.getSkuKeyMap().keySet().iterator().next();
        Long shopId = shopProductSkuKey.getShopId();
        Bargain bargain = bargainService.lambdaQuery()
                .select(Bargain::getStackable)
                .apply("DATE_ADD(end_time, INTERVAL bargain_validity_period HOUR) > NOW()")
                .eq(Bargain::getId, activityId)
                .eq(Bargain::getShopId, shopId)
//                .eq(ClientType.PLATFORM_CONSOLE.equals(ISystem.clientTypeMust()),Bargain::getPlatformDeleteFlag,Boolean.FALSE)
//                .eq(!ClientType.PLATFORM_CONSOLE.equals(ISystem.clientTypeMust()),Bargain::getShopDeleteFlag,Boolean.FALSE)
                .notIn(Bargain::getStatus, Lists.newArrayList(ActivityStatus.ILLEGAL_SELL_OFF, ActivityStatus.SHOP_SELL_OFF))
                .one();
        if (bargain == null) {
            throw new GlobalException(BargainErrorCode.BARGAIN_ACTIVITY_NOT_EXISTS, "砍价活动不存在");
        }
        Long productId = shopProductSkuKey.getProductId();
        BargainOrder bargainOrder = bargainOrderService.lambdaQuery()
                .eq(BargainOrder::getActivityId, activityId)
                .eq(BargainOrder::getSponsorId, userId)
                .eq(BargainOrder::getShopId, shopId)
                .eq(BargainOrder::getProductId, productId)
                .ge(BargainOrder::getEndTime, LocalDateTime.now())
                .ne(BargainOrder::getBargainStatus, BargainStatus.FAILED_TO_BARGAIN)
                .last(SqlHelper.SQL_LIMIT_1)
                .one();
        if (bargainOrder == null) {
            throw new GlobalException("砍价订单不存在或已砍价失败");
        }
        //订单号不为空 该砍价订单已经创建成功
        if (bargainOrder.getOrderNo() != null) {
            throw new GlobalException("您已参加过该商品活动，订单号：" + bargainOrder.getOrderNo());
        }
        Long skuId = shopProductSkuKey.getSkuId();
        // 获取砍价商品信息
        BargainProduct bargainProduct = bargainProductService.lambdaQuery()
                .select(BargainProduct::getFloorPrice, BargainProduct::getSkuPrice)
                .eq(BargainProduct::getActivityId, activityId)
                .eq(BargainProduct::getShopId, shopId)
                .eq(BargainProduct::getProductId, productId)
                .eq(BargainProduct::getSkuId, skuId)
                .one();
        if (bargainProduct == null) {
            throw new GlobalException("非砍价商品，不支持砍价订单");
        }
        Long helpCutAmount = bargainHelpPeopleService.query()
                .select("sum(help_cut_amount) AS help_cut_amount")
                .eq("activity_id", activityId)
                .eq("shop_id", shopId)
                .eq("product_id", productId)
                .one().getHelpCutAmount();
        helpCutAmount = helpCutAmount == null ? 0 : helpCutAmount;
        Long floorPrice = bargainProduct.getFloorPrice();
        if ((floorPrice + helpCutAmount) < bargainProduct.getSkuPrice()) {
            throw new GlobalException("砍价未到底价");
        }
        return new ActivityResp()
                .setSkuKeyPriceMap(Map.of(shopProductSkuKey, floorPrice))
                .setStackable(bargain.getStackable());
    }

    /**
     * 获取砍价活动
     *
     * @return 砍价活动信息
     */
    @Log("订单砍价活动")
    @Override
    @AddonProvider(filter = "BARGAIN", service = Services.GRUUL_MALL_ORDER, supporterId = OrderAddonConstant.ORDER_DISCOUNT_SUPPORT_ID, methodName = "activity")
    public ActivityResp getBargain(ActivityParam param) {
        ActivityResp resp = this.bargainBudget(param);
        ShopProductSkuKey shopProductSkuKey = param.getSkuKeyMap().keySet().iterator().next();
        Long shopId = shopProductSkuKey.getShopId();
        Long productId = shopProductSkuKey.getProductId();
        // 更新订单号
        bargainOrderService.lambdaUpdate()
                .set(BargainOrder::getOrderNo, param.getOrderNo())
                .eq(BargainOrder::getActivityId, param.getActivityId())
                .eq(BargainOrder::getSponsorId, param.getUserId())
                .eq(BargainOrder::getShopId, shopId)
                .eq(BargainOrder::getProductId, productId)
                .update();
        return resp;
    }

    /**
     * todo 优化后重新处理
     */
    @Override
    @AddonProvider(filter = FILTER, service = Services.GRUUL_MALL_GOODS, supporterId = GoodsConstant.ADDON_SUPPORT_ID, methodName = "activity")
    public ActivityDetailVO activity(Long activityId, Long userId, ShopProductSkuKey key) {
        BargainSponsorProductVO bargainProductSku = bargainConsumerService.getBargainProductSku(key.getShopId(), key.getProductId(), userId);
        if (bargainProductSku == null) {
            return null;
        }
        List<BargainProductDetailVO> details = bargainProductSku.getBargainProductDetails();
        if (CollUtil.isEmpty(details)) {
            return null;
        }
        BargainSponsorProductSkuVO sponsor = bargainProductSku.getSponsorProductSku();
        StackableDiscount stackable;
        if (sponsor != null) {
            stackable = sponsor.getStackable();
        } else {
            Bargain activity = bargainService.lambdaQuery()
                    .select(Bargain::getStackable)
                    .eq(BaseEntity::getId, activityId)
                    .eq(Bargain::getShopId, key.getShopId())
                    .one();
            stackable = activity.getStackable();
        }
        BargainProductDetailVO detail = details.iterator().next();
        return new ActivityDetailVO()
                .setType(OrderType.BARGAIN)
                .setActivityId(activityId)
                .setActivityPrice(
                        details.stream()
                                .filter(curDetail -> key.getSkuId().equals(curDetail.getSkuId()))
                                .findFirst()
                                .map(BargainProductDetailVO::getFloorPrice)
                                .orElse(null)
                )
                .setTime(
                        new RangeDateTime().setStart(detail.getStartTime()).setEnd(detail.getEndTime())
                )
                .setStackable(stackable)
                .setData(sponsor == null ? null : new JSONObject(sponsor));
    }


}

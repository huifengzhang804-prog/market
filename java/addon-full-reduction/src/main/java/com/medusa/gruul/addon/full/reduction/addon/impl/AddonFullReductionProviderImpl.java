package com.medusa.gruul.addon.full.reduction.addon.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONObject;
import com.medusa.gruul.addon.full.reduction.addon.AddonFullReductionProvider;
import com.medusa.gruul.addon.full.reduction.components.FullReductionOrderExists;
import com.medusa.gruul.addon.full.reduction.model.bo.MaxFullReduction;
import com.medusa.gruul.addon.full.reduction.model.enums.FullReductionOrderStatus;
import com.medusa.gruul.addon.full.reduction.mp.dao.FullReductionOrderDao;
import com.medusa.gruul.addon.full.reduction.mp.entity.FullReductionOrder;
import com.medusa.gruul.addon.full.reduction.util.FullReductionUtil;
import com.medusa.gruul.common.addon.provider.AddonProvider;
import com.medusa.gruul.common.addon.provider.AddonProviders;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.global.model.constant.Services;
import com.medusa.gruul.goods.api.constant.GoodsConstant;
import com.medusa.gruul.goods.api.model.vo.ProductDiscountVO;
import com.medusa.gruul.order.api.addon.OrderAddonConstant;
import com.medusa.gruul.order.api.addon.fullreduction.FullReductionResponse;
import com.medusa.gruul.order.api.addon.fullreduction.OrderFullReductionParam;
import com.medusa.gruul.order.api.entity.OrderDiscount;
import com.medusa.gruul.order.api.enums.DiscountSourceStatus;
import com.medusa.gruul.order.api.enums.DiscountSourceType;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 满减插件提供器
 *
 * @author wudi
 * @since 2023/3/6
 */
@Service
@DubboService
@AddonProviders
@RequiredArgsConstructor
public class AddonFullReductionProviderImpl implements AddonFullReductionProvider {


    private final FullReductionOrderDao fullReductionOrderDao;
    private final FullReductionOrderExists fullReductionOrderExists;

    /**
     * 获取满减可用的优惠列表
     *
     * @param param 订单满减参数
     * @return 满减活动对应的优惠列表
     */
    @Log("订单使用满减活动")
    @Override
    @AddonProvider(service = Services.GRUUL_MALL_ORDER, supporterId = OrderAddonConstant.ORDER_DISCOUNT_SUPPORT_ID, methodName = "fullReduction")
    public FullReductionResponse getFullReductionsOrderDiscount(OrderFullReductionParam param) {
        Map<Long, Map<Long, Long>> shopProductAmountMap = param.getShopProductAmountMap();
        if (CollUtil.isEmpty(shopProductAmountMap)) {
            return null;
        }
        //是否是预计算
        boolean isBudget = BooleanUtil.isTrue(param.getBudget());
        //满减订单
        List<FullReductionOrder> orders = isBudget ? List.of() : CollUtil.newArrayList();
        //满减折扣项
        Map<Long, OrderDiscount> orderDiscounts = MapUtil.newHashMap();

        String orderNo = param.getOrderNo();
        Long userId = param.getBuyerId();
        shopProductAmountMap.forEach((shopId, productAmountMap) -> {
            // 获取最大的满减优惠
            MaxFullReduction maxFullReduction = FullReductionUtil.maxFullReduction(shopId, productAmountMap);
            // 没有满减优惠
            if (maxFullReduction.getDiscountAmount() == 0L) {
                return;
            }
            Long activityId = maxFullReduction.getActivityId();
            orderDiscounts.put(
                    shopId,
                    new OrderDiscount()
                            .setOrderNo(orderNo)
                            .setSourceType(DiscountSourceType.FULL_REDUCTION)
                            .setSourceStatus(DiscountSourceStatus.OK)
                            .setSourceId(activityId)
                            .setSourceAmount(maxFullReduction.getDiscountAmount())
                            .setTotalAmount(maxFullReduction.getTotalAmount())
                            .setSourceDesc(maxFullReduction.getDesc())
                            .setProductIds(maxFullReduction.getProductIds())
            );
            if (isBudget) {
                return;
            }
            orders.add(
                    new FullReductionOrder()
                            .setShopId(shopId)
                            .setActivityId(activityId)
                            .setUserId(userId)
                            .setOrderNo(orderNo)
                            .setStatus(FullReductionOrderStatus.UNPAID)
            );

        });
        if (MapUtil.isEmpty(orderDiscounts)) {
            return null;
        }
        //如果是预计算 则直接返回结果
        FullReductionResponse result = new FullReductionResponse()
                .setOrderDiscounts(orderDiscounts);
        if (isBudget) {
            return result;
        }
        fullReductionOrderDao.saveBatch(orders);
        //标记满减订单存在
        fullReductionOrderExists.add(orderNo);
        return result;
    }

    @Override
    @AddonProvider(filter = FILTER, service = Services.GRUUL_MALL_GOODS, supporterId = GoodsConstant.ADDON_SUPPORT_ID, methodName = "discount")
    public ProductDiscountVO discount(@Nullable Long userId, ShopProductKey key, Long amount) {
        MaxFullReduction maxFullReduction = FullReductionUtil.maxFullReduction(key.getShopId(), Map.of(key.getProductId(), amount));
        Long discountAmount = maxFullReduction.getDiscountAmount();
        if (discountAmount <= CommonPool.NUMBER_ZERO) {
            return null;
        }
        return new ProductDiscountVO()
                .setDiscount(discountAmount)
                .setData(new JSONObject().putOpt("desc", maxFullReduction.getDesc()));
    }

}

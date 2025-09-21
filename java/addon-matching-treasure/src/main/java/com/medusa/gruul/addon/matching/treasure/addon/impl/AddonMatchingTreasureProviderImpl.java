package com.medusa.gruul.addon.matching.treasure.addon.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.medusa.gruul.addon.matching.treasure.addon.AddonMatchingTreasureProvider;
import com.medusa.gruul.addon.matching.treasure.model.enums.ProductAttributes;
import com.medusa.gruul.addon.matching.treasure.model.enums.SetMealStatus;
import com.medusa.gruul.addon.matching.treasure.model.vo.SetMealProductOrderVO;
import com.medusa.gruul.addon.matching.treasure.mp.entity.SetMeal;
import com.medusa.gruul.addon.matching.treasure.mp.entity.SetMealProduct;
import com.medusa.gruul.addon.matching.treasure.mp.service.ISetMealProductService;
import com.medusa.gruul.addon.matching.treasure.mp.service.ISetMealService;
import com.medusa.gruul.addon.matching.treasure.service.SetMealConsumerService;
import com.medusa.gruul.common.addon.provider.AddonProvider;
import com.medusa.gruul.common.addon.provider.AddonProviders;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.global.model.constant.Services;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.goods.api.model.vo.SetMealBasicInfoVO;
import com.medusa.gruul.order.api.addon.OrderAddonConstant;
import com.medusa.gruul.order.api.addon.activity.ActivityParam;
import com.medusa.gruul.order.api.addon.activity.ActivityResp;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wudi
 * @since 2023/03/21
 */
@Service
@DubboService
@AddonProviders
@RequiredArgsConstructor
public class AddonMatchingTreasureProviderImpl implements AddonMatchingTreasureProvider {

    private final ISetMealService setMealService;
    private final ISetMealProductService setMealProductService;
    private final SetMealConsumerService setMealConsumerService;


    @Override
    @AddonProvider(filter = FILTER, service = Services.GRUUL_MALL_ORDER, supporterId = OrderAddonConstant.ORDER_DISCOUNT_SUPPORT_ID, methodName = "activityBudget")
    public ActivityResp packageBudget(ActivityParam param) {
        return getMatchingTreasure(param);
    }

    /**
     * 套餐活动
     *
     * @param activityParam 套餐活动参数
     * @return 套餐活动信息
     */
    @Override
    @Log("套餐下单")
    @AddonProvider(filter = FILTER, service = Services.GRUUL_MALL_ORDER, supporterId = OrderAddonConstant.ORDER_DISCOUNT_SUPPORT_ID, methodName = "activity")
    public ActivityResp getMatchingTreasure(ActivityParam activityParam) {
        JSONObject obj = activityParam.getExtra();
        JSONArray arrays;
        if (obj == null || (arrays = obj.getJSONArray("setMealProducts")) == null || arrays.isEmpty()) {
            throw new GlobalException("套餐商品不能为空");
        }
        List<SetMealProductOrderVO> orderSetMealProducts = arrays.toList(SetMealProductOrderVO.class);
        // 校验套餐主商品和搭配商品否存在
        SetMealProductOrderVO mainProduct = validSetMealMainAndMatchingProduct(orderSetMealProducts);
        Long shopId = mainProduct.getShopId();
        Long activityId = activityParam.getActivityId();
        LocalDateTime now = LocalDateTime.now();
        SetMeal setMeal = setMealService.lambdaQuery()
                .eq(SetMeal::getId, activityId)
                .eq(SetMeal::getShopId, shopId)
                .ne(SetMeal::getSetMealStatus, SetMealStatus.ILLEGAL_SELL_OFF)
                .le(SetMeal::getStartTime, now)
                .ge(SetMeal::getEndTime, now)
                .one();
        if (setMeal == null) {
            throw new GlobalException("套餐活动不存在或已过期");
        }
        List<SetMealProduct> setMealProducts = setMealProductService.lambdaQuery()
                .eq(SetMealProduct::getSetMealId, activityId)
                .eq(SetMealProduct::getShopId, shopId)
                .list();
        // 获取套餐商品skuKey和价格
        Map<ShopProductSkuKey, Long> skuKeyPriceMap = getSkuKeyPriceMap(orderSetMealProducts, setMealProducts);

        return new ActivityResp()
                .setStackable(setMeal.getStackable())
                .setSkuKeyPriceMap(skuKeyPriceMap);
    }

    /**
     * 商品详情页套餐基本信息
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 套餐基本信息
     */
    @Override
    @Log("商品详情页套餐基本信息")
    @AddonProvider(
            service = Services.GRUUL_MALL_GOODS, supporterId = "goodsAddonSupporter", methodName = "getSetMealBasicInfo")
    public List<SetMealBasicInfoVO> getSetMealBasicInfo(Long shopId, Long productId) {
        return setMealConsumerService.getSetMealBasicInfo(shopId, productId);
    }

    /**
     * 校验套餐主商品和搭配商品是否合理
     *
     * @param packageProducts 套餐商品
     */
    private SetMealProductOrderVO validSetMealMainAndMatchingProduct(List<SetMealProductOrderVO> packageProducts) {
        //主商品数量
        SetMealProductOrderVO mainProduct = null;
        int mainProductCount = 0;
        //搭配商品数量
        int matchingProductCount = 0;
        for (SetMealProductOrderVO packageProduct : packageProducts) {
            ProductAttributes productAttributes = packageProduct.getProductAttributes();
            if (ProductAttributes.MAIN_PRODUCT == productAttributes) {
                mainProductCount++;
                mainProduct = packageProduct;
                continue;
            }
            matchingProductCount++;
        }
        if (mainProductCount != CommonPool.NUMBER_ONE) {
            throw new GlobalException("套餐订单必须只包含一个主商品");
        }
        if (matchingProductCount < CommonPool.NUMBER_ONE || matchingProductCount > CommonPool.NUMBER_FOUR) {
            throw new GlobalException("套餐订单只能包含一个到四个搭配商品");
        }
        return mainProduct;
    }

    /**
     * sku对应的价格 map
     *
     * @param orderSetMealProducts 套餐商品
     * @param setMealProducts      套餐商品
     */
    private Map<ShopProductSkuKey, Long> getSkuKeyPriceMap(List<SetMealProductOrderVO> orderSetMealProducts,
                                                           List<SetMealProduct> setMealProducts) {
        Map<Long, Map<Long, Long>> setMealProductSkuPriceMap = setMealProducts.stream()
                .collect(Collectors.groupingBy(SetMealProduct::getProductId))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().stream()
                        .collect(Collectors.toMap(SetMealProduct::getSkuId, SetMealProduct::getMatchingPrice))));
        return orderSetMealProducts.stream()
                .collect(Collectors.toMap(
                        entry -> new ShopProductSkuKey()
                                .setShopId(entry.getShopId())
                                .setProductId(entry.getProductId())
                                .setSkuId(entry.getSkuId()),
                        entry -> {
                            Map<Long, Long> map = setMealProductSkuPriceMap.get(entry.getProductId());
                            Long price;
                            if (map == null || (price = map.get(entry.getSkuId())) == null) {
                                throw new GlobalException("套餐商品不存在");
                            }
                            return price;
                        }));


    }


}

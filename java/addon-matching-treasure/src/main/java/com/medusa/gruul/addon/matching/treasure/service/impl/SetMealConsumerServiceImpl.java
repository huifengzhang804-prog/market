package com.medusa.gruul.addon.matching.treasure.service.impl;

import com.google.common.collect.Sets;
import com.medusa.gruul.addon.matching.treasure.model.enums.ProductAttributes;
import com.medusa.gruul.addon.matching.treasure.model.enums.SetMealStatus;
import com.medusa.gruul.addon.matching.treasure.model.vo.SetMealDetailVO;
import com.medusa.gruul.addon.matching.treasure.model.vo.SetMealProductDetailVO;
import com.medusa.gruul.addon.matching.treasure.mp.entity.SetMeal;
import com.medusa.gruul.addon.matching.treasure.mp.entity.SetMealProduct;
import com.medusa.gruul.addon.matching.treasure.mp.service.ISetMealProductService;
import com.medusa.gruul.addon.matching.treasure.mp.service.ISetMealService;
import com.medusa.gruul.addon.matching.treasure.service.SetMealConsumerService;
import com.medusa.gruul.addon.matching.treasure.util.SetMealRedisUtils;
import com.medusa.gruul.common.model.base.*;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.vo.SetMealBasicInfoVO;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SetMealConsumerServiceImpl implements SetMealConsumerService {


    private final ISetMealService setMealService;
    private final GoodsRpcService goodsRpcService;
    private final StorageRpcService storageRpcService;
    private final ISetMealProductService setMealProductService;
    private final SetMealManageServiceImpl setMealManageServiceImpl;


    /**
     * 功能描述
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 商品详情套餐基本信息
     */
    @Override
    public List<SetMealBasicInfoVO> getSetMealBasicInfo(Long shopId, Long productId) {
        boolean exist = SetMealRedisUtils.getCacheValue(setMealService, shopId);
        if (!exist) {
            return Collections.emptyList();
        }
//         获取套餐商品
        List<SetMealProduct> setMealProducts = setMealProductService.lambdaQuery()
                .select(SetMealProduct::getSetMealId)
                .eq(SetMealProduct::getShopId, shopId)
                .eq(SetMealProduct::getProductId, productId)
                .eq(SetMealProduct::getProductAttributes, ProductAttributes.MAIN_PRODUCT)
                .list();

        if (CollectionUtils.isEmpty(setMealProducts)) {
            return Collections.emptyList();
        }
        // 商品所在的套餐id
//        Set<Long> setMealIds = setMealProducts.stream().map(SetMealProduct::getSetMealId).collect(Collectors.toSet());
        Set<ActivityShopProductKey> set = setMealProducts.stream().map(setMealProduct -> {
            ActivityShopProductKey key = new ActivityShopProductKey();
            key.setProductId(productId);
            key.setShopId(shopId);
            key.setActivityId(setMealProduct.getSetMealId());
            key.setActivityType(OrderType.PACKAGE);
            return key;
        }).collect(Collectors.toSet());

        Map<ActivityShopProductKey, List<StorageSku>> activityShopProductKeyListMap = storageRpcService.productSkuStockBatch(set);
        if (CollectionUtils.isEmpty(activityShopProductKeyListMap)) {
            return Collections.emptyList();
        }
        Set<Long> setMealIds= Sets.newHashSet();
        activityShopProductKeyListMap.forEach((k,v)->{
            long sum = v.stream().mapToLong(StorageSku::getStock).sum();
            if (sum>0) {
                setMealIds.add(k.getActivityId());
            }
        });
        if (CollectionUtils.isEmpty(setMealIds)) {
            return Collections.emptyList();
        }

        return setMealProductService.getSetMealBasicInfo(setMealIds);
    }


    /**
     * 套餐详情
     *
     * @param shopId    店铺id
     * @param setMealId 套餐id
     * @return 套餐详情
     */
    @Override
    public SetMealDetailVO setMealDetail(Long shopId, Long setMealId) {
        LocalDateTime now = LocalDateTime.now();
        SetMeal setMeal = setMealService.lambdaQuery()
                .select(SetMeal::getId,
                        SetMeal::getShopId,
                        SetMeal::getSetMealName,
                        SetMeal::getSetMealMainPicture,
                        SetMeal::getSetMealDescription,
                        SetMeal::getSetMealType,
                        SetMeal::getEndTime,
                        SetMeal::getStackable,
                        SetMeal::getDistributionMode
                )
                .eq(SetMeal::getId, setMealId)
                .eq(SetMeal::getShopId, shopId)
                .le(SetMeal::getStartTime, now)
                .ge(SetMeal::getEndTime, now)
                .ne(SetMeal::getSetMealStatus, SetMealStatus.ILLEGAL_SELL_OFF)
                .one();
        if (setMeal == null) {
            return null;
        }

        List<SetMealProduct> setMealSkus = setMealProductService.getSetMealProduct(setMealId);
        if (CollectionUtils.isEmpty(setMealSkus)) {
            return null;
        }
        Map<Long, List<SetMealProduct>> setMealProductMap = setMealSkus.stream()
                .collect(Collectors.groupingBy(SetMealProduct::getProductId));
        // 查询商品是否是代销商品 如果是代销商品 取供应商 id
        Map<Long, Long> productIdTargetShopId = goodsRpcService.getProductBatch(
                        setMealProductMap.keySet()
                                .stream()
                                .map(productId -> new ShopProductKey(shopId, productId))
                                .collect(Collectors.toSet())
                ).entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                entry -> entry.getKey().getProductId(),
                                entry -> {
                                    Product product = entry.getValue();
                                    return product.getShopId();
//                                    return SellType.CONSIGNMENT == product.getSellType() ? product.getSupplierId() : product.getShopId();
                                }
                        )
                );

        // 获取商品sku库存
        Map<ActivityShopProductKey, List<StorageSku>> productKeySkusMap = storageRpcService.productSkuStockBatch(
                setMealProductMap.keySet()
                        .stream()
                        .map(productId -> {
                            ActivityShopProductKey activityShopProductKey = new ActivityShopProductKey();
                            activityShopProductKey.setProductId(productId)
                                    .setShopId(productIdTargetShopId.getOrDefault(productId, shopId))
                                    .setActivityId(setMealId)
                                    .setActivityType(OrderType.PACKAGE);
                            return activityShopProductKey;
                        }).collect(Collectors.toSet())

        );
        // 最多可省
        AtomicLong saveAtLeast = new AtomicLong();
        List<SetMealProductDetailVO> setMealProductDetails = getSetMealProductDetail(setMealProductMap, productKeySkusMap, productIdTargetShopId, saveAtLeast);
        return new SetMealDetailVO()
                .setSetMealId(setMeal.getId())
                .setShopId(setMeal.getShopId())
                .setSetMealName(setMeal.getSetMealName())
                .setSetMealMainPicture(setMeal.getSetMealMainPicture())
                .setSetMealDescription(setMeal.getSetMealDescription())
                .setSetMealType(setMeal.getSetMealType())
                .setSaveAtLeast(saveAtLeast.get())
                .setEndTime(setMeal.getEndTime())
                .setStackable(setMeal.getStackable())
                .setDistributionMode(setMeal.getDistributionMode())
                .setSetMealProductDetails(setMealProductDetails);
    }

    private List<SetMealProductDetailVO> getSetMealProductDetail(
            Map<Long, List<SetMealProduct>> setMealProductMap,
            Map<ActivityShopProductKey, List<StorageSku>> productKeySkusMap,
            Map<Long, Long> productIdTargetShopId,
            AtomicLong saveAtLeast
    ) {

        Map<ShopProductSkuKey, StorageSku> skuIdSkuMap = productKeySkusMap.values()
                .stream()
                .flatMap(Collection::stream)
                .toList()
                .stream().
                collect(Collectors.toMap(sku -> new ShopProductSkuKey(sku.getShopId(), sku.getProductId(), sku.getId()), v -> v));
        List<SetMealProductDetailVO> list = setMealProductMap.entrySet()
                .stream()
                .map(entry -> {
                    SetMealProduct setMealProduct = entry.getValue().get(CommonPool.NUMBER_ZERO);
                    return new SetMealProductDetailVO()
                            .setProductId(entry.getKey())
                            .setProductName(setMealProduct.getProductName())
                            .setProductPic(setMealProduct.getProductPic())
                            .setProductAttributes(setMealProduct.getProductAttributes())
                            .setSetMealProductSkuDetails(
                                    entry.getValue()
                                            .stream()
                                            .map(setMealProductSku -> {
                                                StorageSku storageSku = skuIdSkuMap.get(new ShopProductSkuKey(productIdTargetShopId.getOrDefault(entry.getKey(), setMealProduct.getShopId()), entry.getKey(), setMealProductSku.getSkuId()));
                                                SetMealProductDetailVO.SetMealProductSkuDetailVO detail = new SetMealProductDetailVO.SetMealProductSkuDetailVO();
                                                detail.setSkuId(setMealProductSku.getSkuId())
                                                        .setMatchingPrice(setMealProductSku.getMatchingPrice())
                                                        .setSaveAtLeast(setMealProductSku.getSaveAtLeast());
                                                if (storageSku != null) {
                                                    detail.setMatchingStock(storageSku.getStock())
                                                            .setStockType(storageSku.getStockType())
                                                            .setSkuName(storageSku.getSpecs())
                                                            .setStorageSku(storageSku);
                                                }
                                                saveAtLeast.addAndGet(setMealProductSku.getSaveAtLeast());
                                                return detail;
                                            }).collect(Collectors.toList())
                            );

                }).toList();
        // 过滤掉没有库存的套餐商品
        list=list.stream().filter(x->{
            long sum = x.getSetMealProductSkuDetails().stream().mapToLong(y -> y.getStorageSku().getStock()).sum();
            if (sum>0) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }).toList();
        return list;
    }
}

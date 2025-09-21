package com.medusa.gruul.cart.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.TypeReference;
import com.medusa.gruul.cart.api.dto.ShopIdSkuIdsDTO;
import com.medusa.gruul.cart.service.model.bo.ShopProductSkuValid;
import com.medusa.gruul.cart.service.model.dto.ProductSkuDTO;
import com.medusa.gruul.cart.service.model.vo.ProductSkuVO;
import com.medusa.gruul.cart.service.mq.CartRabbitService;
import com.medusa.gruul.cart.service.service.CartService;
import com.medusa.gruul.cart.service.service.ShopProductSkuValidService;
import com.medusa.gruul.cart.service.util.CartUtil;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.FeatureValueDTO;
import com.medusa.gruul.goods.api.model.dto.ProductFeaturesValueDTO;
import com.medusa.gruul.order.api.helper.OrderHelper;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import com.medusa.gruul.storage.api.entity.StorageSku;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2022/5/16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final ShopRpcService shopRpcService;
    private final CartRabbitService cartRabbitService;
    private final ShopProductSkuValidService shopProductSkuValidService;


    @Override
    public void addCart(ProductSkuDTO productSku) {
        this.editCartProductSku(null, productSku);
    }

    @Override
    public void editCartProductSku(String uniqueId, ProductSkuDTO productSku) {
        Long userId = ISecurity.userMust().getId();
        Long shopId = productSku.getShopId();
        Long productId = productSku.getProductId();
        Long currentSkuId = productSku.getSkuId();
        AtomicInteger shopCountNum = new AtomicInteger(productSku.getNum());
        int num = productSku.getNum();
        if (num <= 0) {
            return;
        }
        Set<ProductFeaturesValueDTO> productAttribute = productSku.getProductAttributes();
        /* 检查店铺
         */
        ShopProductSkuValid validResult = shopProductSkuValidService.validShop(shopId);

        if (validResult.getException() != null) {
            throw validResult.getException();
        }
        // 获取店铺类型
        ShopMode mode = validResult.getShopInfo().getShopMode();


        validResult = shopProductSkuValidService.validShopProductSku(shopId, productId, currentSkuId, num, productAttribute);
        if (validResult.getException() != null) {
            throw validResult.getException();
        }

        /* 查询缓存数据
         */

        List<ProductSkuVO> cartShopProductSkus = CartUtil.getCartShopProductSkus(userId, productSku.getShopId(), mode);
        if (CollUtil.isEmpty(cartShopProductSkus)) {
            cartShopProductSkus = new ArrayList<>();
        }

        /* 用于判断是否添加了同样的商品  商品id -> 规格id -> 属性信息
         * 1.之前已经添加了同样的商品,则把数量相加
         * 2.商品状态改变可能为已失效的商品,新加入商品则进行区分 放入缓存集合
         * 3.未添加过商品则把商品信息放入缓存集合里去
         */
        Optional<ProductSkuVO> first = cartShopProductSkus
                .stream()
                .filter(innerSku -> {
                    if (!productId.equals(innerSku.getProductId())) {
                        return false;
                    }
                    if (!currentSkuId.equals(innerSku.getId())) {
                        return false;
                    }
                    //相同唯一id视为编辑该条数据
                    if (uniqueId != null && uniqueId.equals(innerSku.getUniqueId())) {
                        return false;
                    }
                    // 相同的属性信息视为相同商品
                    Set<ProductFeaturesValueDTO> innerAttr = innerSku.getProductAttributes();
                    if (CollUtil.isEmpty(innerAttr) && CollUtil.isEmpty(productAttribute)) {
                        return true;
                    }
                    return CollUtil.emptyIfNull(innerAttr).equals(productAttribute);
                })
                .findFirst();
        boolean present = first.isPresent();
        // 存在相同的商品 数量相加
        if (present) {
            num = num + first.get().getNum();
            //不存在相同商品表示 编辑sku 则过滤掉老数据
        }
        if (uniqueId != null) {
            cartShopProductSkus = cartShopProductSkus.stream()
                    .filter(
                            item -> {
                                if (uniqueId.equals(item.getUniqueId())) {
                                    shopCountNum.getAndAdd(-item.getNum());
                                    return false;
                                }
                                return true;
                            }
                    )
                    .collect(Collectors.toList());
        }
        validResult = shopProductSkuValidService.validShopProductSku(shopId, productId, currentSkuId, num, productAttribute);
        if (validResult.getException() != null) {
            throw validResult.getException();
        }

        Product productInfo = validResult.getProductInfo();
        StorageSku storageSku = validResult.getStorageSku();
        long sum = 0;
        if (productAttribute != null) {
            sum = productAttribute
                    .stream()
                    .flatMap(productFeaturesValue -> productFeaturesValue.getFeatureValues().stream())
                    .mapToLong(FeatureValueDTO::getSecondValue)
                    .sum();
        }
        ProductSkuVO currentProductSku = CartUtil.sku2CartProduct(present ? first.get() : new ProductSkuVO(), productInfo, storageSku, num, productAttribute);
        currentProductSku.setFinalPrice(currentProductSku.getSalePrice() + sum);
        if (!present) {
            cartShopProductSkus.add(currentProductSku);
        }
        //更新缓存
        CartUtil.cacheCartShopProductSkus(userId, shopId, cartShopProductSkus, mode);
        //更新店铺的加购数
        Map<Long, List<ProductSkuVO>> shopCountMap = new HashMap<>();
        shopCountMap.put(
                shopId,
                new ArrayList<>() {
                    {
                        add(new ProductSkuVO().setNum(shopCountNum.get()));
                    }
                }
        );
        //更新 店铺加购数
        cartRabbitService.sendShopCartUpdate(shopCountMap);
    }

    @Override
    public void deleteCartProduct(Long userId, Long shopId, List<ShopIdSkuIdsDTO> shopIdSkuIds) {
        if (CollUtil.isEmpty(shopIdSkuIds)) {
            return;
        }
        log.debug("\n 删除购物车商品：{}", shopIdSkuIds);
        ShopMode mode = CartUtil.checkMode(shopRpcService, shopId);
        String key = CartUtil.myCartCacheKey(userId, shopId, mode);
        //店铺加购数 删除map
        Map<Long, List<ProductSkuVO>> shopCountMap = new HashMap<>();
        for (ShopIdSkuIdsDTO shopIdSkuId : shopIdSkuIds) {
            String shopIdStr = shopIdSkuId.getShopId().toString();
            //O2O店铺下单 下单删除购物车bug修复
            if (null == shopId && StrUtil.isNotEmpty(shopIdStr)) {
                shopId = Long.valueOf(shopIdStr);
                mode = CartUtil.checkMode(shopRpcService, shopId);
                key = CartUtil.myCartCacheKey(userId, shopId, mode);
            }
            List<ProductSkuVO> skuCaches = RedisUtil.getCacheMapValue(key, shopIdStr, new TypeReference<>() {
            });
            if (CollUtil.isEmpty(skuCaches)) {
                continue;
            }
            Map<Long, List<List<String>>> skuSpecsMap = MapUtil.emptyIfNull(shopIdSkuId.getSkuSpecsMap());
            Set<String> uniqueIds = shopIdSkuId.getUniqueIds();
            boolean uniqueIdsNotEmpty = CollUtil.isNotEmpty(uniqueIds);
            List<ProductSkuVO> newCache = skuCaches.stream()
                    .filter(skuCache -> {
                        //如果唯一id不为空 则 过滤掉包含得数据
                        if (uniqueIdsNotEmpty) {
                            //不包含 返回true
                            return !uniqueIds.contains(skuCache.getUniqueId());
                        }
                        //通过规格和属性过滤
                        List<List<String>> specsList = skuSpecsMap.get(skuCache.getId());
                        //如果未取到 则返回true  true 表示不过滤
                        if (specsList == null) {
                            return true;
                        }
                        return !specAttrMatched(specsList, skuCache.getSpecs(), skuCache.getProductAttributes());

                    })
                    .collect(Collectors.toList());
            if (CollUtil.isEmpty(newCache)) {
                RedisUtil.delCacheMapValue(key, shopIdStr);
                //处理店铺加购数、删除数据
                handleShopCountMap(shopId, shopCountMap, skuCaches);
                continue;
            }
            RedisUtil.setCacheMapValue(key, shopIdStr, newCache);
            //处理店铺加购数、更新时间
            handleShopCountMap(shopId, shopCountMap, skuCaches.stream().filter(skuCache -> !newCache.contains(skuCache)).toList());
        }
        //删除店铺加购数
        cartRabbitService.sendShopCartUpdate(shopCountMap);
    }


    /**
     * 规格和属性是否已匹配
     * todo 计算过程过于复杂 待优化，想想有没有更好的方式
     *
     * @param attrSpecsList     带属性得规格
     * @param skuSpecs          sku原始规格
     * @param productAttributes sku属性
     * @return 是否匹配
     */
    private boolean specAttrMatched(List<List<String>> attrSpecsList, List<String> skuSpecs, Set<ProductFeaturesValueDTO> productAttributes) {
        for (List<String> attrSpecs : attrSpecsList) {
            //如果完全相等 -》匹配
            if (attrSpecs.equals(skuSpecs)) {
                return true;
            }
            //sku原始规格排在前面
            // 如果是同一个规格 则 attrSpecs 和原始规格完全相等
            List<String> attrSpec = attrSpecs.subList(skuSpecs.size(), attrSpecs.size());
            if (CollUtil.isEmpty(attrSpec)) {
                return true;
            }
            if (CollUtil.isEmpty(productAttributes)) {
                continue;
            }
            //购物车里的商品属性规格
            List<String> cartSkuAttrSpecs = productAttributes.stream()
                    .filter(feature -> CollUtil.isNotEmpty(feature.getFeatureValues()))
                    .flatMap(
                            feature -> feature.getFeatureValues()
                                    .stream()
                                    .map(attr -> OrderHelper.attr2Spec(attr.getFirstValue(), attr.getSecondValue()))
                    ).toList();
            //如果商品 属性对应的规格完全一致 说明匹配
            if (cartSkuAttrSpecs.size() == attrSpec.size() && new HashSet<>(cartSkuAttrSpecs).containsAll(attrSpec)) {
                return true;
            }
        }
        return false;
    }

    private void handleShopCountMap(Long shopId,
                                    Map<Long, List<ProductSkuVO>> shopUpdateCountMap,
                                    List<ProductSkuVO> skuCaches) {
        List<ProductSkuVO> productSkus = shopUpdateCountMap.get(shopId);
        if (CollUtil.isNotEmpty(productSkus)) {
            productSkus.add(new ProductSkuVO().setNum(-skuCaches.stream().mapToInt(ProductSkuVO::getNum).sum()));
        } else {
            shopUpdateCountMap.put(
                    shopId,
                    new ArrayList<>() {
                        {
                            add(new ProductSkuVO().setNum(-skuCaches.stream().mapToInt(ProductSkuVO::getNum).sum()));
                        }
                    }
            );
        }
    }


    @Override
    public void clear(Long shopId) {
        ShopMode mode = CartUtil.checkMode(shopRpcService, shopId);
        RedisUtil.delete(
                CartUtil.myCartCacheKey(ISecurity.userMust().getId(), shopId, mode)
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public void clearInvalid(Long currentShopId) {
        ShopMode mode = CartUtil.checkMode(shopRpcService, currentShopId);
        String key = CartUtil.myCartCacheKey(ISecurity.userMust().getId(), currentShopId, mode);
        Map<Long, List<ProductSkuVO>> shopProductsMap = RedisUtil.getCacheMap(key, new TypeReference<>() {
        });
        if (CollUtil.isEmpty(shopProductsMap)) {
            return;
        }
        Map<String, List<ProductSkuVO>> newShopProductsMap = new HashMap<>(CommonPool.NUMBER_THIRTY);
        shopProductsMap.forEach(
                (shopId, productSkus) -> {
                    //校验店铺
                    ShopProductSkuValid shopValidResult = shopProductSkuValidService.validShop(shopId);
                    if (shopValidResult.getException() != null) {
                        return;
                    }
                    //校验SKU
                    if (CollUtil.isEmpty(productSkus)) {
                        return;
                    }
                    String shopIdStr = String.valueOf(shopId);
                    productSkus.forEach(
                            productSku -> {
                                ShopProductSkuValid validResult = shopProductSkuValidService.validShopProductSku(shopId, productSku.getProductId(), productSku.getId(), productSku.getNum(), productSku.getProductAttributes());
                                if (validResult.getException() != null) {
                                    return;
                                }
                                List<ProductSkuVO> innerProductSkus = newShopProductsMap.get(shopIdStr);
                                if (CollUtil.isNotEmpty(innerProductSkus)) {
                                    innerProductSkus.add(productSku);
                                    return;
                                }
                                newShopProductsMap.put(
                                        shopIdStr,
                                        new ArrayList<>(CommonPool.NUMBER_THIRTY) {
                                            {
                                                add(productSku);
                                            }
                                        }
                                );
                            }
                    );

                }
        );
        if (CollUtil.isEmpty(newShopProductsMap)) {
            RedisUtil.delete(key);
            //删除店铺加购数计数
            cartRabbitService.sendShopCartUpdate(getShopCountMap(shopProductsMap));
            return;
        }

        RedisUtil.executePipelined(
                operation -> operation.delete(key),
                operation -> operation.opsForHash().putAll(key, newShopProductsMap)
        );
        //删除店铺加购数计数
        cartRabbitService.sendShopCartUpdate(getShopCountMap(shopProductsMap));
        //更新店铺加购数计数
        cartRabbitService.sendShopCartUpdate(
                newShopProductsMap.entrySet().stream()
                        .collect(Collectors.toMap(
                                        productKey -> Long.valueOf(productKey.getKey()),
                                        Map.Entry::getValue
                                )
                        )
        );
    }

    private Map<Long, List<ProductSkuVO>> getShopCountMap(Map<Long, List<ProductSkuVO>> shopProductsMap) {
        return shopProductsMap.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                value -> {
                    List<ProductSkuVO> productSkus = value.getValue();
                    //负数
                    productSkus.forEach(productSku -> productSku.setNum(-productSku.getNum()));
                    return productSkus;
                }
        ));
    }


    /**
     * 修改店铺购物车缓存
     *
     * @param shopCountMap 店铺购物车计数map
     */
    @Override
    public void updateShopCount(Map<Long, List<ProductSkuVO>> shopCountMap) {
        //一个服务消费，可用不考虑原子性，多个服务需要考虑原子性
        shopCountMap.forEach((shopId, productSkus) -> {
            Integer count = productSkus.stream().mapToInt(ProductSkuVO::getNum).sum();
            if (!count.equals(CommonPool.NUMBER_ZERO)) {
                Integer shopCount = CartUtil.getCacheShopCount(shopId);
                int calCount = Math.max(shopCount + count, CommonPool.NUMBER_ZERO);
                CartUtil.cacheShopCount(shopId, calCount);
            }
        });
    }


}

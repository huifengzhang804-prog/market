package com.medusa.gruul.cart.service.util;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.TypeReference;
import com.medusa.gruul.cart.service.model.constant.CartConst;
import com.medusa.gruul.cart.service.model.vo.ProductSkuVO;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.ProductFeaturesValueDTO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import com.medusa.gruul.storage.api.entity.StorageSku;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author 张治保
 * date 2022/5/18
 */
public final class CartUtil {
    private CartUtil() {
    }

    public static ShopMode checkMode(ShopRpcService shopRpcService, Long currentShopId) {
        if (currentShopId == null) {
            return ShopMode.COMMON;
        }
        return shopRpcService.getShopInfoByShopId(currentShopId).getShopMode();
    }

    public static void cacheCartShopProductSkus(Long userId, Long shopId, List<ProductSkuVO> shopProductSkus) {
        RedisUtil.setCacheMapValue(
                CartUtil.myCartCacheKey(userId),
                String.valueOf(shopId),
                shopProductSkus
        );
    }

    public static void cacheCartShopProductSkus(Long userId, Long shopId, List<ProductSkuVO> shopProductSkus, ShopMode mode) {
        RedisUtil.setCacheMapValue(
                CartUtil.myCartCacheKey(userId, shopId, mode),
                String.valueOf(shopId),
                shopProductSkus
        );
    }

    /**
     * 设置店铺购物车计数缓存
     *
     * @param shopId        店铺id
     * @param shopCartCount 店铺购物车计数
     */
    public static void cacheShopCount(Long shopId, Integer shopCartCount) {
        RedisUtil.setCacheMapValue(
                CartUtil.shopCountCartCacheKey(),
                String.valueOf(shopId),
                shopCartCount
        );
    }

    /**
     * 获取店铺加购数计数
     *
     * @param shopId 店铺id
     * @return 购物车计数
     */
    public static Integer getCacheShopCount(Long shopId) {
        return (Integer) Optional.ofNullable(RedisUtil.getCacheMapValue(
                CartUtil.shopCountCartCacheKey(),
                String.valueOf(shopId)
        )).orElse(CommonPool.NUMBER_ZERO);
    }

    /**
     * 获取购物车店铺的商品sku列表
     */
    public static List<ProductSkuVO> getCartShopProductSkus(Long userId, Long shopId, ShopMode mode) {
        return RedisUtil.getCacheMapValue(
                CartUtil.myCartCacheKey(userId, shopId, mode),
                String.valueOf(shopId),
                new TypeReference<>() {
                }
        );
    }


    /**
     * 获取我的购物车详情
     */
    public static Map<Long, List<ProductSkuVO>> getMyCartDetail(Long userId) {
        return RedisUtil.getCacheMap(
                CartUtil.myCartCacheKey(userId),
                new TypeReference<>() {
                }
        );
    }

    /**
     * 获取我的购物车详情
     */
    public static Map<Long, List<ProductSkuVO>> getMyCartDetail(Long userId, ShopMode mode, Long currentShopId) {
        return RedisUtil.getCacheMap(
                CartUtil.myCartCacheKey(userId, currentShopId, mode),
                new TypeReference<>() {
                }
        );
    }


    /**
     * sku缓存数据转 购物车缓存数据
     *
     * @param productSku 购物车缓存的数据
     * @param storage    sku数据
     * @return 购物车缓存数据
     */
    public static ProductSkuVO sku2CartProduct(ProductSkuVO productSku, Product product, StorageSku storage, Integer num, Set<ProductFeaturesValueDTO> productFeaturesValueDTOS) {
        productSku
                .setProductId(storage.getProductId())
                .setDistributionMode(product.getDistributionMode())
                .setProductName(product.getName())
                .setProductImage(product.getPic())
                .setFreightTemplateId(product.getFreightTemplateId())
                .setId(storage.getId())
                .setImage(storage.getImage())
                .setPrice(storage.getPrice())
                .setSalePrice(storage.getSalePrice())
                .setNum(num)
                .setSpecs(storage.getSpecs())
                .setWeight(storage.getWeight())
                .setSellType(product.getSellType())
                .setSupplierId(product.getSupplierId())
                .setProductAttributes(productFeaturesValueDTOS)
                .setConsignmentPriceSetting(product.getExtra().getConsignmentPriceSetting())
                .setUniqueId(productSku.getUniqueId() == null ?
                        IdUtil.getSnowflakeNextIdStr() : productSku.getUniqueId());
        return productSku;
    }

    public static void main(String[] args) {
        System.out.println(IdUtil.getSnowflakeNextIdStr());
        System.out.println(IdUtil.fastUUID());
    }

    /**
     * 我的购物车缓存key
     */
    public static String myCartCacheKey(Long userId, Long shopId, ShopMode mode) {
        return RedisUtil.key(
                CartConst.MY_CART_CACHE_KEY,
                userId,
                mode,
                mode == ShopMode.COMMON ? CommonPool.NUMBER_ZERO : shopId
        );
    }

    /**
     * 店铺计数加购数 缓存key
     *
     * @return cacheKey
     */
    public static String shopCountCartCacheKey() {
        return RedisUtil.key(
                CartConst.SHOP_COUNT_CART_KEY
        );
    }

    public static String myCartCacheKey(Long userId) {
        return RedisUtil.key(
                CartConst.MY_CART_CACHE_KEY,
                userId
        );
    }
}

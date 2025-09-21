package com.medusa.gruul.cart.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import com.medusa.gruul.cart.api.constant.CartErrorCode;
import com.medusa.gruul.cart.service.addon.CartAddonSupport;
import com.medusa.gruul.cart.service.model.bo.ShopProductSkuValid;
import com.medusa.gruul.cart.service.model.bo.ValidatedShop;
import com.medusa.gruul.cart.service.model.bo.ValidatedShopProduct;
import com.medusa.gruul.cart.service.model.constant.CartConst;
import com.medusa.gruul.cart.service.model.vo.CartVO;
import com.medusa.gruul.cart.service.model.vo.ProductSkuVO;
import com.medusa.gruul.cart.service.model.vo.ShopProductSkuVO;
import com.medusa.gruul.cart.service.model.vo.SkuStockVO;
import com.medusa.gruul.cart.service.service.CartQueryService;
import com.medusa.gruul.cart.service.service.ShopProductSkuValidService;
import com.medusa.gruul.cart.service.util.CartUtil;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.FeatureValueDTO;
import com.medusa.gruul.goods.api.model.dto.ProductFeaturesValueDTO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import com.medusa.gruul.storage.api.entity.StorageSku;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author 张治保
 * date 2022/5/16
 */
@Service
@RequiredArgsConstructor
public class CartQueryServiceImpl implements CartQueryService {

    private final ShopProductSkuValidService shopProductSkuValidService;

    private final ShopRpcService shopRpcService;

    private final CartAddonSupport cartAddonSupport;


    @Override
    public CartVO myCart(Long currentShopId) {
        Long userId = ISecurity.userMust().getId();
        ShopMode mode = CartUtil.checkMode(shopRpcService, currentShopId);

        Map<Long, List<ProductSkuVO>> shopIdProductSkuMap = CartUtil.getMyCartDetail(userId, mode, currentShopId);
        CartVO cart = new CartVO();
        if (MapUtil.isEmpty(shopIdProductSkuMap)) {
            return cart;
        }

        shopIdProductSkuMap.forEach(
                (shopId, skus) -> {
                    ValidatedShop validatedShop = validatedShop(shopId, skus);
                    if (validatedShop.getValid() != null) {
                        cart.getValid().add(validatedShop.getValid());
                    }
                    if (validatedShop.getInvalid() != null) {
                        cart.getInvalid().add(validatedShop.getInvalid());
                    }
                }
        );
        return cart;
    }

    @Override
    public Integer cartCount(Long shopId) {
        ShopMode mode = CartUtil.checkMode(shopRpcService, shopId);
        Map<Long, List<ProductSkuVO>> shopIdProductSkuMap = CartUtil.getMyCartDetail(ISecurity.userMust().getId(), mode, shopId);
        return MapUtil.emptyIfNull(shopIdProductSkuMap)
                .values()
                .stream()
                .map(
                        productSku -> CollUtil.emptyIfNull(productSku).size()
                )
                .reduce(Integer::sum)
                .orElse(0);
    }

    /**
     * 店铺购物车金额计算
     *
     * @param shopId 店铺id
     * @return 店铺购物车总金额
     */
    @Override
    public Long cartTotalMoney(Long shopId) {
        Long userId = ISecurity.userMust().getId();
        Map<Long, List<ProductSkuVO>> shopIdProductSkuMap = CartUtil.getMyCartDetail(userId, ShopMode.O2O, shopId);
        if (MapUtil.isEmpty(shopIdProductSkuMap)) {
            return (long)CommonPool.NUMBER_ZERO;
        }
        AtomicLong totalSalePrice = new AtomicLong();
        shopIdProductSkuMap.forEach(
                (currentShopId, skus) -> {
                    ValidatedShop validatedShop = validatedShop(currentShopId, skus);
                    if (validatedShop.getValid() != null) {
                        totalSalePrice.addAndGet(validatedShop.getValid().getProducts().stream()
                                .mapToLong(productSkuVO -> {
                                    long salePrice = productSkuVO.getSalePrice();
                                    Set<ProductFeaturesValueDTO> attributes = productSkuVO.getProductAttributes();
                                    long secondValueSum = 0;
                                    if (CollUtil.isNotEmpty(attributes)) {
                                        secondValueSum = attributes.stream()
                                                .flatMapToLong(productFeaturesValue ->
                                                        Optional.ofNullable(productFeaturesValue.getFeatureValues())
                                                                .orElse(Collections.emptySet())
                                                                .stream()
                                                                .mapToLong(FeatureValueDTO::getSecondValue))
                                                .sum();
                                    }
                                    // (实际价格*属性参数金额)*选购数量
                                    return Math.max((salePrice + secondValueSum) * productSkuVO.getNum(), 0);
                                })
                                .sum());
                    }
                }
        );
        return totalSalePrice.get();
    }


    /**
     * 校验购物车店铺商品的数据是否可用
     */
    private ValidatedShop validatedShop(Long shopId, List<ProductSkuVO> productSkus) {

        ValidatedShop validatedShop = new ValidatedShop();
        List<ProductSkuVO> validShopProducts = new ArrayList<>();
        List<ProductSkuVO> invalidShopProducts = new ArrayList<>();

        //校验店铺是否可用
        ShopProductSkuValid validResult = shopProductSkuValidService.validShop(shopId);
        ShopInfoVO shopInfo = validResult.getShopInfo();
        if (validResult.getException() != null) {
            invalidShopProducts.addAll(productSkus);
            return validatedShop.setInvalid(
                    new ShopProductSkuVO()
                            .setShopId(shopId)
                            .setShopName(shopInfo == null ? CartConst.UNKNOWN_SHOP_NAME : shopInfo.getName())
                            .setShopMode(Objects.nonNull(shopInfo)?shopInfo.getShopMode():null)
                            .setShopType(Objects.nonNull(shopInfo)?shopInfo.getShopType():null)
                            .setEnable(Boolean.FALSE)
                            .setProducts(invalidShopProducts)
            );
        }
        String shopName = shopInfo.getName();
        String shoLogo = shopInfo.getLogo();
        //遍历商品确定是否可用
        productSkus.forEach(
                sku -> {
                    ValidatedShopProduct validatedShopProduct = validatedShopProduct(shopId, sku);
                    if (validatedShopProduct.getValid() != null) {
                        validShopProducts.add(validatedShopProduct.getValid());
                        return;
                    }
                    invalidShopProducts.add(validatedShopProduct.getInvalid());
                }
        );
        //正常商品
        if (CollUtil.isNotEmpty(validShopProducts)) {
            //获取未失效的商品中有同城配送方式的商品
            List<ProductSkuVO> list = validShopProducts.stream().filter(x -> x.getDistributionMode().contains(DistributionMode.INTRA_CITY_DISTRIBUTION)).toList();
            if (CollectionUtil.isNotEmpty(list)) {
                Boolean supportIc = cartAddonSupport.queryShopIcStatusForCart(shopId);
                if (BooleanUtil.isFalse(supportIc)) {
                    for (ProductSkuVO sku : list) {
                        sku.getDistributionMode().remove(DistributionMode.INTRA_CITY_DISTRIBUTION);
                        if (CollectionUtil.isEmpty(sku.getDistributionMode())) {
                            validShopProducts.remove(sku);
                            invalidShopProducts.add(sku);
                         }
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(validShopProducts)) {
                validatedShop.setValid(
                        new ShopProductSkuVO()
                                .setShopId(shopId)
                                .setShopName(shopName)
                                .setShopLogo(shoLogo)
                                .setShopMode(Objects.nonNull(shopInfo)?shopInfo.getShopMode():null)
                                .setShopType(Objects.nonNull(shopInfo)?shopInfo.getShopType():null)
                                .setEnable(Boolean.TRUE)
                                .setProducts(validShopProducts)
                );
            }
        }
        //失效商品
        if (CollUtil.isNotEmpty(invalidShopProducts)) {
            validatedShop.setInvalid(
                    new ShopProductSkuVO()
                            .setShopId(shopId)
                            .setShopName(shopName)
                            .setShopLogo(shoLogo)
                            .setEnable(Boolean.TRUE)
                            .setShopMode(Objects.nonNull(shopInfo)?shopInfo.getShopMode():null)
                            .setShopType(Objects.nonNull(shopInfo)?shopInfo.getShopType():null)
                            .setProducts(invalidShopProducts)
            );
        }
        return validatedShop;
    }

    /**
     * 判断单个商品的可用状态
     */
    private ValidatedShopProduct validatedShopProduct(Long shopId, ProductSkuVO sku) {

        ValidatedShopProduct validatedShopProduct = new ValidatedShopProduct();

        Long productId = sku.getProductId();
        Long skuId = sku.getId();
        Integer buyNumber = sku.getNum();
        Set<ProductFeaturesValueDTO> productAttributes = sku.getProductAttributes();
        /*
         *  校验商品 是否有效
         */
        ShopProductSkuValid validResult = shopProductSkuValidService.validShopProductSku(shopId, productId, skuId, buyNumber, productAttributes);

        /*
         * 取结果
         */
        GlobalException exception = validResult.getException();
        Product productInfo = validResult.getProductInfo();
        StorageSku storage = validResult.getStorageSku();

        return (exception == null || CartErrorCode.OUT_OF_STOCK == exception.code()) ?
                validatedShopProduct.setValid(
                        sku.toEdit(productInfo, storage)
                                .setSpecs(storage.getSpecs())
                                .setImage(storage.getImage())
                                .setSalePrice(storage.getSalePrice())
                                .setPrice(storage.getPrice())
                                .setProductAttributes(sku.getProductAttributes())
                                .setUniqueId(sku.getUniqueId())
                                .setNeedUpdateNum(exception != null && CartErrorCode.OUT_OF_STOCK == exception.code())
                                .setSkuStock(
                                        new SkuStockVO()
                                                .setLimitType(storage.getLimitType())
                                                .setLimitNum(storage.getLimitNum())
                                                .setStockType(storage.getStockType())
                                                .setStock(storage.getStock())
                                )


                ) :
                validatedShopProduct.setInvalid(
                        sku.toInvalid()
                                .setReason(exception.code())
                );
    }

}

package com.medusa.gruul.order.service.modules.order.service.impl;

import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.rpc.Good2OrderRpcService;
import com.medusa.gruul.order.service.model.dto.OrderShopsDTO;
import com.medusa.gruul.order.service.model.dto.OrderValidDTO;
import com.medusa.gruul.order.service.model.dto.ProductDTO;
import com.medusa.gruul.order.service.model.enums.OrderError;
import com.medusa.gruul.order.service.model.vo.OrderValidVO;
import com.medusa.gruul.order.service.modules.order.service.CreateOrderService;
import com.medusa.gruul.order.service.modules.order.service.OrderValidService;
import com.medusa.gruul.order.service.mp.service.IShopOrderItemService;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.api.enums.LimitType;
import com.medusa.gruul.storage.api.enums.StockType;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * @since 2024/4/2
 */
@Service
@RequiredArgsConstructor
public class OrderValidServiceImpl implements OrderValidService {

    private final ShopRpcService shopRpcService;
    private final StorageRpcService storageRpcService;
    private final Good2OrderRpcService good2OrderRpcService;
    private final IShopOrderItemService shopOrderItemService;
    private final CreateOrderService createOrderService;

    @Override
    public void orderValid(OrderValidDTO orderValid) {
        int productNum = orderValid.getProducts().stream().mapToInt(OrderValidDTO.ShopProduct::getNum).sum();
        createOrderService.validMiniAppOrderCount(productNum, orderValid.getDistributionMode());

        //获取所有商品涉及的店铺 id
        Set<OrderValidDTO.ShopProduct> products = orderValid.getProducts();
        /* 校验店铺禁用情况
         */
        Set<Long> shopIds = products.stream()
                .map(OrderValidDTO.ShopProduct::getKey)
                .map(ShopProductSkuKey::getShopId)
                .collect(Collectors.toSet());
        this.validShops(shopIds);

        /* 校验商品禁用情况
         */
        //渲染商品 key
        Set<ShopProductKey> productKeys = products.stream()
                .map(OrderValidDTO.ShopProduct::getKey)
                .map(key -> new ShopProductKey(key.getShopId(), key.getProductId()))
                .collect(Collectors.toSet());
        Map<Long, Map<Long, Product>> shopProductMap = this.validProducts(productKeys);
        /* 校验库存与限购
         */
        Map<ShopProductSkuKey, Integer> skuKeyNumMap = products.stream()
                .collect(Collectors.toMap(OrderValidDTO.ShopProduct::getKey, OrderValidDTO.ShopProduct::getNum));
        OrderType orderType = orderValid.getOrderType();
        Long activityId = orderValid.activityId();
        Map<ShopProductSkuKey, ActivityShopProductSkuKey> skuKeyMap = skuKeyNumMap.keySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                key -> key,
                                key -> {
                                    ActivityShopProductSkuKey skuKey = (ActivityShopProductSkuKey) new ActivityShopProductSkuKey()
                                            .setSkuId(key.getSkuId())
                                            .setProductId(key.getProductId())
                                            .setShopId(key.getShopId())
                                            .setActivityType(orderType)
                                            .setActivityId(activityId);
                                    Product product = shopProductMap.get(key.getShopId()).get(key.getProductId());
                                    //非活动的代销商品 店铺ID是供应商ID 需要替换 而活动类代销商品 店铺ID是创建活动的店铺ID 不需要替换
                                    if (SellType.CONSIGNMENT.equals(product.getSellType())&&OrderType.COMMON.equals(orderType)) {
                                        skuKey.setShopId(product.getSupplierId());
                                    }
                                    return skuKey;
                                }
                        )
                );
        //查询库存
        Map<ActivityShopProductSkuKey, StorageSku> skuMap = storageRpcService.skuBatch(new HashSet<>(skuKeyMap.values()));
        //不存在的 SKU key
        Set<ShopProductSkuKey> notExists = new HashSet<>();
        Set<OrderValidVO.StockNotEnoughSku> stockNotEnough = new HashSet<>();
        Set<OrderValidVO.LimitNotEnoughSku<?>> limitNotEnough = new HashSet<>();
        Map<ShopProductKey, Integer> boughtProductNumberMap = new HashMap<>();
        Long userId = ISecurity.userMust().getId();
        //库存不足的 sku
        skuKeyMap.forEach(
                (key, activityShopProductSkuKey) -> {
                    //获取库存数据
                    StorageSku storageSku = skuMap.get(activityShopProductSkuKey);
                    Integer curNum = skuKeyNumMap.get(key);
                    if (storageSku == null) {
                        notExists.add(key);
                        return;
                    }
                    //校验库存是否充足
                    if (StockType.LIMITED == storageSku.getStockType() && storageSku.getStock() < curNum) {
                        stockNotEnough.add(
                                new OrderValidVO.StockNotEnoughSku()
                                        .setKey(key)
                                        .setStock(storageSku.getStock())
                        );
                    }
                    //校验限购
                    //如果不限购 直接返回
                    if (LimitType.UNLIMITED == storageSku.getLimitType()) {
                        return;
                    }
                    //获取当前商品购买总数
                    ShopProductKey productKey = key.toShopProductKey();
                    Integer currentProductCount = boughtProductNumberMap.compute(productKey, (k, v) -> v == null ? curNum : v + curNum);
                    //如果是商品限购
                    if (LimitType.PRODUCT_LIMITED == storageSku.getLimitType()) {
                        Long boughtCount = TenantShop.disable(() -> this.shopOrderItemService.getProductBoughtNumCount(userId, productKey));
                        if (isOverLimit(currentProductCount, boughtCount, storageSku.getLimitNum())) {
                            limitNotEnough.add(
                                    new OrderValidVO.LimitNotEnoughSku<>()
                                            .setKey(productKey)
                                            .setBought(boughtCount)
                                            .setLimit(storageSku.getLimitNum())
                            );
                        }
                        return;
                    }
                    //如果是SKU限购
                    Long boughtCount = TenantShop.disable(() -> this.shopOrderItemService.getProductSkuBoughtNumCount(userId, key));
                    if (isOverLimit(currentProductCount, boughtCount, storageSku.getLimitNum())) {
                        limitNotEnough.add(
                                new OrderValidVO.LimitNotEnoughSku<>()
                                        .setKey(key)
                                        .setBought(boughtCount)
                                        .setLimit(storageSku.getLimitNum())
                        );
                    }
                }
        );
        //如果不存在的商品、库存不足的商品、限购不足的商品都为空 则直接返回
        if (notExists.isEmpty() && stockNotEnough.isEmpty() && limitNotEnough.isEmpty()) {
            return;
        }
        //否则抛出异常
        throw OrderError.SKU_NOT_AVAILABLE.dataEx(
                new OrderValidVO()
                        .setNotExists(notExists)
                        .setStockNotEnough(stockNotEnough)
                        .setLimitNotEnough(limitNotEnough)
        );
    }

    /**
     * 校验店铺可用状态
     *
     * @param shopIds 店铺id集合
     */
    private void validShops(Set<Long> shopIds) {
        //查询店铺信息
        List<ShopInfoVO> shopInfos = shopRpcService.getShopInfoByShopIdList(shopIds);
        Map<Long, ShopInfoVO> shopInfoMap = shopInfos.stream()
                .collect(
                        Collectors.toMap(ShopInfoVO::getId, Function.identity())
                );

        //禁用的店铺
        Set<Long> forbiddenShopIds = shopIds.stream()
                .filter(shopId -> {
                    ShopInfoVO shopInfo = shopInfoMap.get(shopId);
                    //如果店铺信息不存在 或 店铺处于非可正常售卖商品状态
                    return shopInfo == null || ShopStatus.NORMAL != shopInfo.getStatus();
                })
                .collect(Collectors.toSet());
        if (!forbiddenShopIds.isEmpty()) {
            throw OrderError.SHOP_NOT_AVAILABLE.dataEx(forbiddenShopIds);
        }
    }

    private Map<Long, Map<Long, Product>> validProducts(Set<ShopProductKey> productKeys) {
        //批量获取商品信息
        Map<Long, Map<Long, Product>> shopProductMap = good2OrderRpcService.productBatch(productKeys);
        //用于存储不可售卖商品的商品 key
        Set<ShopProductKey> forbiddenProductKeys = productKeys.stream()
                .filter(
                        key -> {
                            Map<Long, Product> shopProducts = shopProductMap.get(key.getShopId());
                            return shopProducts == null || shopProducts.get(key.getProductId()) == null;
                        }
                ).collect(Collectors.toSet());
        if (!forbiddenProductKeys.isEmpty()) {
            throw OrderError.GOODS_NOT_AVAILABLE.dataEx(forbiddenProductKeys);
        }
        return shopProductMap;
    }

    /**
     * 是否超限购
     */
    private boolean isOverLimit(Integer currentCount, Long boughtCount, Integer limitCount) {
        return currentCount + boughtCount > limitCount;
    }


}

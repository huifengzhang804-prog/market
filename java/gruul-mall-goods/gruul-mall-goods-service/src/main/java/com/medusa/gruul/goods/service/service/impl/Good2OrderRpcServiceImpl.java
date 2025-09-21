package com.medusa.gruul.goods.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrPool;
import com.alibaba.fastjson2.JSONObject;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.mp.model.DS;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.rpc.Good2OrderRpcService;
import com.medusa.gruul.goods.service.mp.service.IProductService;
import com.medusa.gruul.goods.service.util.GoodsUtil;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商品对 订单下单 功能支持 性能优先
 *
 * @author 张治保
 * @apiNote 注重性能实现 避免干扰订单
 * @since 2024/2/18
 */
@Service
@DubboService
@RequiredArgsConstructor
public class Good2OrderRpcServiceImpl implements Good2OrderRpcService {

    private final IProductService productService;

    @Override
    public Map<Long, Map<Long, Product>> productBatch(Set<ShopProductKey> productKeys) {
        if (CollUtil.isEmpty(productKeys)) {
            return Map.of();
        }
        //批量查询缓存中的商品
        Map<Long, Map<Long, Product>> productsCache = getProductsCache(productKeys);
        if (productKeys.size() == productsCache.values().stream().mapToLong(Map::size).sum()) {
            return productsCache;
        }
        //数据库查询 需要去数据库查询的商品 key
        Set<ShopProductKey> needLoadKeys = productKeys.stream()
                .filter(key -> {
                    Map<Long, Product> shopProducts = productsCache.get(key.getShopId());
                    return shopProducts == null || shopProducts.get(key.getProductId()) == null;
                })
                .collect(Collectors.toSet());
        List<Product> loadProducts = TenantShop.disable(
                //查询商品字段 和 缓存获取字段一致  OrderProductFields.ORDER_FIELDS
                () -> DS.sharding(() -> productService.lambdaQuery()
                        .select(
                                Product::getShopId, Product::getId, Product::getDistributionMode, Product::getSellType,
                                Product::getExtra, Product::getServiceIds, Product::getFreightTemplateId, Product::getSupplierId,
                                Product::getStatus, Product::getName, Product::getPic, Product::getProductType
                        )
                        //(shop_id,id) in ((1,1),(1,2),(1,3))
                        .apply("(shop_id,id) in (" + needLoadKeys.stream().map(key -> "(" + key.getShopId() + StrPool.COMMA + key.getProductId() + ")").collect(Collectors.joining(StrPool.COMMA)) + ")")
                        .list()
                )
        );
        if (CollUtil.isEmpty(loadProducts)) {
            return productsCache;
        }
        for (Product loadProduct : loadProducts) {
            productsCache.computeIfAbsent(loadProduct.getShopId(), k -> MapUtil.newHashMap()).put(loadProduct.getId(), loadProduct);
        }
        return productsCache;
    }

    /**
     * 获取缓存中的商品
     *
     * @param productKeys ShopProductKey
     * @return List<Product>
     */
    @SuppressWarnings({"unchecked"})
    private Map<Long, Map<Long, Product>> getProductsCache(Set<ShopProductKey> productKeys) {
        Set<String> keys = productKeys.stream()
                .map(productKey -> GoodsUtil.productCacheKey(productKey.getShopId(), productKey.getProductId()))
                .collect(Collectors.toSet());

        //获取缓存中的商品
        List<Object> caches = RedisUtil.executePipelined(redisOperations -> {
            HashOperations<String, String, Object> opsForHash = redisOperations.opsForHash();
            for (String key : keys) {
                opsForHash.multiGet(key, OrderProductFields.ORDER_FIELDS);
            }
        });
        Map<Long, Map<Long, Product>> result = MapUtil.newHashMap();
        //转换缓存结果为商品
        for (Object cacheValue : caches) {
            List<Object> orderValues = (List<Object>) cacheValue;
            if (CollUtil.isEmpty(orderValues)) {
                continue;
            }
            JSONObject productMap = OrderProductFields.toProductMap(orderValues);
            if (productMap.isEmpty()) {
                continue;
            }
            Product product = productMap.to(Product.class);
            result.computeIfAbsent(product.getShopId(), k -> MapUtil.newHashMap()).put(product.getId(), product);
        }
        return result;
    }


    /**
     * 订单商品字段
     */
    interface OrderProductFields {

        /**
         * 订单下单需要的数据
         */
        List<String> ORDER_FIELDS = List.of(
                "shopId",
                "id",
                "distributionMode",
                "sellType",
                "extra",
                "name",
                "pic",
                "serviceIds",
                "freightTemplateId",
                "supplierId",
                "status",
                "productType"
        );

        /**
         * 缓存查询结果转换为商品 JSON map
         *
         * @param orderValues 查询结果
         * @return 商品 JSON map
         */
        static JSONObject toProductMap(List<Object> orderValues) {
            JSONObject jsonObject = new JSONObject();
            for (int index = 0; index < ORDER_FIELDS.size(); index++) {
                Object value = orderValues.get(index);
                if (value == null) {
                    continue;
                }
                jsonObject.put(ORDER_FIELDS.get(index), value);
            }
            return jsonObject;
        }

    }
}

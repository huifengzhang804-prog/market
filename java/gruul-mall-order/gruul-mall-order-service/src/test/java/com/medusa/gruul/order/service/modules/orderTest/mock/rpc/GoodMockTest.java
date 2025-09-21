package com.medusa.gruul.order.service.modules.orderTest.mock.rpc;

import cn.hutool.core.map.MapUtil;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.global.model.enums.ServiceBarrier;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.ProductExtraDTO;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.rpc.Good2OrderRpcService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * @author 张治保
 * @since 2024/2/20
 */
public class GoodMockTest implements Good2OrderRpcService {

    /**
     * key = shopId + "-" + productId
     */
    private final BiFunction<Long, Long, String> key = (shopId, productId) -> shopId + "-" + productId;

    private final Map<String, Map> dataBase = new HashMap<>();

    {
        Long shopId1 = 1L, productId1 = 1L;
        dataBase.put(
                key.apply(shopId1, productId1),
                Map.of(
                        shopId1,
                        Map.of(
                                productId1,
                                (Product) new Product()
                                        .setShopId(shopId1)
                                        .setDistributionMode(List.of(DistributionMode.EXPRESS))
                                        .setSellType(SellType.OWN)
                                        .setExtra(new ProductExtraDTO())
                                        .setName("测试商品")
                                        .setServiceIds(List.of(ServiceBarrier.ALL_ENSURE))
                                        .setFreightTemplateId(1L)
                                        .setSupplierId(1L)
                                        .setStatus(ProductStatus.SELL_ON)
                                        .setId(productId1)


                        )
                )
        );
    }


    /**
     * "shopId",
     * "id",
     * "distributionMode",
     * "sellType",
     * "extra",
     * "name",
     * "serviceIds",
     * "freightTemplateId",
     * "supplierId",
     * "status"
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<Long, Map<Long, Product>> productBatch(Set<ShopProductKey> shopProductKeys) {
        Map<Long, Map<Long, Product>> result = MapUtil.newHashMap();
        for (ShopProductKey shopProductKey : shopProductKeys) {
            result.putAll(dataBase.get(key.apply(shopProductKey.getShopId(), shopProductKey.getProductId())));
        }
        return result;
    }
}

package com.medusa.gruul.storage.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.global.config.Global;
import com.medusa.gruul.global.model.helper.CompletableTask;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.storage.api.bo.StSvBo;
import com.medusa.gruul.storage.api.dto.ShopProductKeyDTO;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.service.addon.StorageAddonSupporter;
import com.medusa.gruul.storage.service.components.StorageSupplierExists;
import com.medusa.gruul.storage.service.model.bo.UpdateStockOrder;
import com.medusa.gruul.storage.service.mp.entity.StorageDetail;
import com.medusa.gruul.storage.service.mp.service.IStorageDetailService;
import com.medusa.gruul.storage.service.mp.service.IStorageSkuService;
import com.medusa.gruul.storage.service.service.SkuStockDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2023/8/3
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SkuStockDetailServiceImpl implements SkuStockDetailService {

    private final Executor globalExecutor;
    private final GoodsRpcService goodsRpcService;
    private final StorageAddonSupporter storageAddonSupporter;
    private final IStorageDetailService storageDetailService;
    private final IStorageSkuService storageSkuService;
    private final StorageSupplierExists storageSupplierExists;

    @Override
    @Async(Global.GLOBAL_EXECUTOR)
    public void generateStockDetails(List<UpdateStockOrder> generateDetailsOrders) {
        if (CollUtil.isEmpty(generateDetailsOrders)) {
            return;
        }
        Set<Long> shopIds = CollUtil.newHashSet();
        Set<ShopProductKey> shopProductKeys = CollUtil.newHashSet();
        Set<ActivityShopProductSkuKey> skuKeys = CollUtil.newHashSet();
        for (UpdateStockOrder order : generateDetailsOrders) {
            for (ActivityShopProductSkuKey skuKey : order.getStSvMap().keySet()) {
                skuKeys.add(skuKey);
                shopIds.add(skuKey.getShopId());
                shopProductKeys.add(skuKey.toShopProductKey());
            }
        }
        //过滤除供应商 id
        Set<Long> supplierIds = storageSupplierExists.exists(shopIds);
        //根据是否是供应商分组
        Map<Boolean, Set<ShopProductKey>> isSupplierProductMap = shopProductKeys.stream()
                .collect(
                        Collectors.groupingBy(
                                productKey -> supplierIds.contains(productKey.getShopId()),
                                Collectors.toSet()
                        )
                );
        //供应商商品 key
        Set<ShopProductKey> supplierProductKeys = isSupplierProductMap.get(Boolean.TRUE);
        //普通店铺商品 key
        Set<ShopProductKey> finalShopProductKeys = isSupplierProductMap.get(Boolean.FALSE);
        Map<ShopProductKey, Product> allProducts = MapUtil.newHashMap();
        Map<ActivityShopProductSkuKey, StorageSku> skusBatch = MapUtil.newHashMap();

        //用于组装任务
        List<Runnable> tasks = new ArrayList<>();
        //查询 sku 信息
        //todo 这个属于可优化的地方 可能调用方已经查询了一次数据，可以传过来，如果为空再进行查询
        tasks.add(() -> skusBatch.putAll(storageSkuService.getSkusBatchMap(skuKeys)));
        //查询供应商商品
        if (CollUtil.isNotEmpty(supplierProductKeys)) {
            Map<ShopProductKey, Product> supplierProductMap = storageAddonSupporter.getSupplierProductBatch(supplierProductKeys);
            if (MapUtil.isNotEmpty(supplierProductMap)) {
                tasks.add(() -> allProducts.putAll(supplierProductMap));
            }
        }
        //查询普通店铺商品
        if (CollUtil.isNotEmpty(shopProductKeys)) {
            tasks.add(() -> allProducts.putAll(goodsRpcService.getProductListBatch(finalShopProductKeys)));
        }
        //等待所有任务执行完毕
        CompletableTask.getOrThrowException(
                CompletableTask.allOf(globalExecutor, tasks.toArray(Runnable[]::new))
        );
        //渲染库存明细数据
        List<StorageDetail> storageDetails = generateDetailsOrders.stream()
                .flatMap(
                        order ->
                                order.getStSvMap()
                                        .entrySet()
                                        .stream()
                                        .map(entry -> {
                                            ActivityShopProductSkuKey key = entry.getKey();
                                            StSvBo value = entry.getValue();
                                            String productName = "unknown";
                                            SellType sellType = SellType.OWN;
                                            Product product = allProducts.get(key.toShopProductKey());
                                            if (product != null) {
                                                productName = product.getName();
                                                sellType = product.getSellType();
                                            }
                                            StorageSku storageSku = skusBatch.get(key);
                                            return new StorageDetail()
                                                    .setShopId(key.getShopId())
                                                    .setSkuId(key.getSkuId())
                                                    .setProductId(key.getProductId())
                                                    .setStockChangeType(order.getChangeType())
                                                    .setOrderNo(order.getOrderNo())
                                                    .setStockChangeNum(value.getStock())
                                                    .setSpecName(storageSku == null ? List.of() : storageSku.getSpecs())
                                                    .setProductName(productName)
                                                    .setSellType(sellType);
                                        })

                ).toList();
        storageDetailService.saveBatch(storageDetails);
    }


    @Override
    public void generateStorageDetail(Map<Long, Set<Long>> productMap) {
        List<ShopProductKeyDTO> shopProductKeyList = productMap.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(productId -> {
                            ShopProductKeyDTO shopProductKey = new ShopProductKeyDTO();
                            shopProductKey.setProductId(productId).setShopId(entry.getKey());
                            return shopProductKey;
                        })
                )
                .toList();

        storageDetailService.updateStorageDetail(shopProductKeyList);

    }

}

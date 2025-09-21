package com.medusa.gruul.storage.service.mq.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.storage.api.bo.StSvBo;
import com.medusa.gruul.storage.api.dto.ProductPriceUpdateDTO;
import com.medusa.gruul.storage.api.enums.StorageRabbit;
import com.medusa.gruul.storage.service.mq.service.StorageMqService;
import com.medusa.gruul.storage.service.util.StorageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * @author 张治保
 * date 2023/8/3
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorageMqServiceImpl implements StorageMqService {

    private final RabbitTemplate rabbitTemplate;
    private final Executor globalExecutor;

    @Override
    public void sendUpdateStockMsg(Map<ActivityShopProductSkuKey, StSvBo> skuKeyStSvs) {
        log.debug("发送库存更新通知 skuKeyStSvs:{}", skuKeyStSvs);
        globalExecutor.execute(
                () -> {
                    //更新数据库库存 mq
                    rabbitTemplate.convertAndSend(
                            StorageRabbit.UPDATE_SKU_STOCK.exchange(),
                            StorageRabbit.UPDATE_SKU_STOCK.routingKey(),
                            skuKeyStSvs
                    );
                    //发送商品库存更新mq 不包含活动商品
                    updateProductStock(skuKeyStSvs);
                }
        );
    }

    /**
     * 分发更新商品库存的消息
     */
    @SuppressWarnings("unchecked")
    private void updateProductStock(Map<ActivityShopProductSkuKey, StSvBo> skuKeyStSvs) {
        //更新
        Set<ActivityShopProductSkuKey> keys = skuKeyStSvs.keySet();

        keys.forEach(
                key -> {
                    if (key.getActivityType().isActivity()) {
                        skuKeyStSvs.remove(key);
                    }
                }
        );
        if (skuKeyStSvs.isEmpty()) {
            return;
        }
        //剩下的全是没有参加活动的商品
        List<ShopProductKey> shopProductKeys = skuKeyStSvs.keySet()
                .stream()
                .map(ActivityShopProductSkuKey::toShopProductKey)
                .distinct()
                .toList();
        //查询供应商被代销商品的店铺 id集合
        //主要为了更新代销商品ES库存
        Map<ShopProductKey, Set<Long>> shopProductKeyShopIdsMap = StorageUtil.consignmentRelationShopIds(shopProductKeys);

        Map<ShopProductSkuKey, StSvBo> productStockUpdateMap = new HashMap<>();
        skuKeyStSvs.forEach(
                (key, stsv) -> {
                    ShopProductSkuKey shopProductSkuKey = key.toShopProductSkuKey();
                    StSvBo curStsv = productStockUpdateMap.getOrDefault(shopProductSkuKey, new StSvBo())
                            .incrementStock(stsv.getStock())
                            .incrementSales(stsv.getSales());
                    Set<Long> shopIdSet = shopProductKeyShopIdsMap.get(key.toShopProductKey());

                    //供应商被代销商品
                    if (CollUtil.isNotEmpty(shopIdSet)) {
                        for (Long shopId : shopIdSet) {
                            productStockUpdateMap.put(
                                    new ShopProductSkuKey(shopId, shopProductSkuKey.getProductId(), shopProductSkuKey.getSkuId()),
                                    curStsv
                            );
                        }
                    }
                    productStockUpdateMap.put(shopProductSkuKey, curStsv);
                }
        );
        log.debug("发送商品库存更新消息:{}", productStockUpdateMap);
        rabbitTemplate.convertAndSend(
                StorageRabbit.UPDATE_PRODUCT_STOCK.exchange(),
                StorageRabbit.UPDATE_PRODUCT_STOCK.routingKey(),
                productStockUpdateMap
        );
    }

    @Override
    public void sendUpdateSkuPriceMsg(ProductPriceUpdateDTO priceUpdate) {
        rabbitTemplate.convertAndSend(
                StorageRabbit.UPDATE_SKU_PRICE.exchange(),
                StorageRabbit.UPDATE_SKU_PRICE.routingKey(),
                priceUpdate
        );
    }
}

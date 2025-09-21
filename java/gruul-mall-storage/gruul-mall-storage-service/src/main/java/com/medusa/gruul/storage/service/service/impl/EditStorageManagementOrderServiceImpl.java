package com.medusa.gruul.storage.service.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.storage.api.bo.StSvBo;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.api.enums.StockChangeType;
import com.medusa.gruul.storage.api.enums.StockType;
import com.medusa.gruul.storage.api.enums.StorageManagementOrderStatus;
import com.medusa.gruul.storage.service.model.bo.UpdateStockOrder;
import com.medusa.gruul.storage.service.model.constant.StorageConstant;
import com.medusa.gruul.storage.service.model.dto.SkuStockItemDTO;
import com.medusa.gruul.storage.service.model.dto.StorageManagementOrderDTO;
import com.medusa.gruul.storage.service.model.enums.StorageError;
import com.medusa.gruul.storage.service.model.enums.StorageManagementOrderType;
import com.medusa.gruul.storage.service.mp.entity.StorageManagementOrder;
import com.medusa.gruul.storage.service.mp.entity.StorageManagementOrderItem;
import com.medusa.gruul.storage.service.mp.service.IStorageManagementOrderItemService;
import com.medusa.gruul.storage.service.mp.service.IStorageManagementOrderService;
import com.medusa.gruul.storage.service.service.EditStorageManagementOrderService;
import com.medusa.gruul.storage.service.service.SkuStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 创建仓储管理订单serviceImpl
 *
 * @author xiaoq
 * @since 2023-07-25 17:30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EditStorageManagementOrderServiceImpl implements EditStorageManagementOrderService {

    private final IStorageManagementOrderService storageManagementOrderService;

    private final SkuStockService skuStockService;

    private final IStorageManagementOrderItemService storageManagementOrderItemService;

    /**
     * 仓储管理订单创建
     *
     * @param storageManagementOrder 仓储管理订单DTO
     * @return 订单号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = StorageConstant.CACHE_KEY_MANAGEMENT_PRODUCT_SKU_STORAGE, batchParamName = "#productIds", key = "#item")
    public String createStorageManagementOrder(StorageManagementOrderDTO storageManagementOrder, Set<Long> productIds) {
        StorageManagementOrder newStorageManagementOrder = new StorageManagementOrder();
        Long shopId = ISecurity.userMust().getShopId();
        //生成订单号
        String no = StorageConstant.STORAGE_MANAGEMENT_ORDER_NO_PREFIX + IdUtil.getSnowflakeNextIdStr();
        //计算库存
        Long totalNum = calculateStock(storageManagementOrder.getStorageManagementOrderItems(), storageManagementOrder.getStockChangeType());

        /*创建主订单
         */
        newStorageManagementOrder.setNo(no)
                .setOperationUserId(ISecurity.userMust().getId())
                .setOperationPhone(ISecurity.userMust().getMobile())
                .setStatus(StorageManagementOrderStatus.WAIT_COMPLETION)
                .setRemark(storageManagementOrder.getRemark())
                .setStorageManagementOrderType(storageManagementOrder.getStorageManagementOrderType())
                .setShopId(shopId)
                .setStockChangeType(storageManagementOrder.getStockChangeType())
                .setInventoryArea(storageManagementOrder.getInventoryArea())
                .setEvidence(storageManagementOrder.getEvidence())
                .setChangeStockTotal(totalNum);

        List<StorageManagementOrderItem> storageManagementOrderItems = new ArrayList<>();
        /* 库存校验
         */
        checkStorage(storageManagementOrder.getStorageManagementOrderItems(), storageManagementOrderItems, no, shopId, storageManagementOrder.getStockChangeType());
        storageManagementOrderService.save(newStorageManagementOrder);
        storageManagementOrderItemService.saveBatch(storageManagementOrderItems);
        return no;
    }

    @Override
    public void cancelStorageManagementOrder(Long id) {
        StorageManagementOrder storageManagementOrder = checkStorageManagementOrder(id);
        storageManagementOrder.setStatus(StorageManagementOrderStatus.CANCEL).setOperationAccomplishTime(LocalDateTime.now());
        storageManagementOrderService.updateById(storageManagementOrder);
    }

    private StockChangeType currentStockChangeType(StorageManagementOrderType orderType, StockChangeType stockChangeType, Long num) {
        return orderType.getChangeable() ?
                num < 0 ? StockChangeType.SHORTAGE_OUTBOUND : StockChangeType.OVERAGE_INBOUND : stockChangeType;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = StorageConstant.STORAGE_MANAGEMENT_ORDER, key = "#id")
    public void completeStorageManagementOrder(Long id) {
        StorageManagementOrder order = checkStorageManagementOrder(id);
        Long shopId = order.getShopId();
        LocalDateTime now = LocalDateTime.now();
        boolean update = storageManagementOrderService.lambdaUpdate()
                .set(StorageManagementOrder::getStatus, StorageManagementOrderStatus.COMPLETION)
                .set(StorageManagementOrder::getOperationAccomplishTime, now)
                .set(StorageManagementOrder::getUpdateTime, now)
                .eq(StorageManagementOrder::getId, id)
                .eq(StorageManagementOrder::getShopId, shopId)
                .eq(StorageManagementOrder::getStatus, StorageManagementOrderStatus.WAIT_COMPLETION)
                .update();
        if (!update) {
            return;
        }

        List<StorageManagementOrderItem> orderItems = storageManagementOrderItemService.lambdaQuery()
                .eq(StorageManagementOrderItem::getShopId, shopId)
                .eq(StorageManagementOrderItem::getOrderNo, order.getNo())
                .list();
        StockChangeType stockChangeType = order.getStockChangeType();
        StorageManagementOrderType orderType = order.getStorageManagementOrderType();
        /* 库存校验
         */
        Boolean changeable = orderType.getChangeable();
        Map<ActivityShopProductSkuKey, SkuStockItemDTO> skuStockMap = orderItems
                .stream()
                .flatMap(orderItem -> orderItem.getSkuStockItems().stream())
                .filter(skuStockItem -> skuStockItem.getStockType() == StockType.LIMITED)
                .collect(Collectors.toMap(
                        stockItem -> (ActivityShopProductSkuKey) new ActivityShopProductSkuKey()
                                .setSkuId(stockItem.getSkuId())
                                .setProductId(stockItem.getProductId())
                                .setShopId(shopId)
                                .setActivityType(OrderType.COMMON)
                                .setActivityId(0L),
                        stockItem -> stockItem
                ));
        //批量查询sku信息
        Map<ActivityShopProductSkuKey, StorageSku> skuMap = skuStockService.getSkusBatch(skuStockMap.keySet());
        //
        Map<StockChangeType, Map<ActivityShopProductSkuKey, StSvBo>> updateStockMap = MapUtil.newHashMap();
        skuStockMap.forEach(
                (key, stockItem) -> {
                    //库存变化量
                    Long stockIncrement = stockItem.getNum();
                    StorageSku storageSku = stockCompare(key, stockItem, stockChangeType, skuMap);
                    stockItem.setStock(storageSku.getStock());
                    if (changeable) {
                        stockIncrement = (stockItem.getNum() - storageSku.getStock());
                    }
                    if (stockIncrement == 0) {
                        return;
                    }
                    StockChangeType changeType = this.currentStockChangeType(orderType, stockChangeType, stockIncrement);
                    Map<ActivityShopProductSkuKey, StSvBo> stsvMap = updateStockMap.computeIfAbsent(changeType, curKey -> MapUtil.newHashMap());
                    StSvBo stSvBo = stsvMap.computeIfAbsent(key, curKey -> new StSvBo());
                    stSvBo.incrementStock(stockIncrement);
                }
        );
        //更新订单数据
        //更新订单项数据
        if (changeable) {
            storageManagementOrderItemService.updateBatchById(orderItems);
        }
        //库存无变化 不生成库存详情
        if (MapUtil.isEmpty(updateStockMap)) {
            return;
        }
        //更新库存
        List<UpdateStockOrder> orderStocks = updateStockMap.entrySet()
                .stream()
                .map(entry -> new UpdateStockOrder()
                        .setOrderNo(order.getNo())
                        .setGenerateDetail(true)
                        .setChangeType(entry.getKey())
                        .setStSvMap(entry.getValue())
                )
                .toList();
        skuStockService.updateStock(
                //忽略缓存不存在的 sku
                true,
                //不可能是代销商品库存
                false,
                orderStocks
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = StorageConstant.CACHE_KEY_MANAGEMENT_PRODUCT_SKU_STORAGE, batchParamName = "#productIds", key = "#item")
    public void editStorageManagementOrder(StorageManagementOrderDTO storageManagementOrder, Set<Long> productIds) {
        Long id = storageManagementOrder.getId();
        // 检验订单
        StorageManagementOrder oldStorageManagementOrder = checkStorageManagementOrder(id);
        String no = oldStorageManagementOrder.getNo();
        Long shopId = oldStorageManagementOrder.getShopId();
        // 计算库存
        Long totalNum = calculateStock(storageManagementOrder.getStorageManagementOrderItems(), storageManagementOrder.getStockChangeType());

        /*修改主订单
         */
        oldStorageManagementOrder.setRemark(storageManagementOrder.getRemark()).setStorageManagementOrderType(storageManagementOrder.getStorageManagementOrderType()).setInventoryArea(storageManagementOrder.getInventoryArea()).setEvidence(storageManagementOrder.getEvidence()).setStockChangeType(storageManagementOrder.getStockChangeType()).setChangeStockTotal(totalNum);
        storageManagementOrderService.updateById(oldStorageManagementOrder);

        List<StorageManagementOrderItem> storageManagementOrderItems = new ArrayList<>();
        /* 库存校验
         */
        checkStorage(storageManagementOrder.getStorageManagementOrderItems(), storageManagementOrderItems, no, shopId, storageManagementOrder.getStockChangeType());
        // 先删除之前关联库存关联订单的商品信息
        storageManagementOrderItemService.lambdaUpdate().eq(StorageManagementOrderItem::getOrderNo, no).eq(StorageManagementOrderItem::getShopId, shopId).remove();
        storageManagementOrderItemService.saveBatch(storageManagementOrderItems);
    }


    public StorageManagementOrder checkStorageManagementOrder(Long id) {
        Long shopId = ISecurity.userMust().getShopId();
        StorageManagementOrder storageManagementOrder = storageManagementOrderService.lambdaQuery().eq(BaseEntity::getId, id).eq(StorageManagementOrder::getShopId, shopId).eq(StorageManagementOrder::getStatus, StorageManagementOrderStatus.WAIT_COMPLETION).one();
        if (storageManagementOrder == null) {
            throw new GlobalException("当前状态订单不存在,或当前状态订单不支持修改");
        }
        return storageManagementOrder;
    }


    public void checkStorage(List<StorageManagementOrderItem> storageManagementOrderItemsList, List<StorageManagementOrderItem> storageManagementOrderItems, String no, Long shopId, StockChangeType stockChangeType) {
        /* 库存校验
         */
        storageManagementOrderItemsList.forEach(storageManagementOrderItem -> {
            //assembleSkuMap
            Map<ActivityShopProductSkuKey, SkuStockItemDTO> skuStockMap = assembleSkuMap(storageManagementOrderItem, shopId);
            // 查询未参加活动的sku信息
            Map<ActivityShopProductSkuKey, StorageSku> skuMap = skuStockService.getSkusBatch(skuStockMap.keySet());
            for (Map.Entry<ActivityShopProductSkuKey, SkuStockItemDTO> entry : skuStockMap.entrySet()) {
                /* 库存比较
                 */
                StorageSku storageSku = stockCompare(entry.getKey(), entry.getValue(), stockChangeType, skuMap);
                entry.getValue().setSpecs(storageSku.getSpecs());

            }
            StorageManagementOrderItem orderItem = new StorageManagementOrderItem();
            orderItem
                    .setOrderNo(no)
                    .setProductId(storageManagementOrderItem.getProductId())
                    .setPic(storageManagementOrderItem.getPic())
                    .setProductName(storageManagementOrderItem.getProductName())
                    .setShopId(shopId)
                    .setSkuStockItems(storageManagementOrderItem.getSkuStockItems());
            storageManagementOrderItems.add(orderItem);
        });
    }


    /**
     * 库存计算
     *
     * @param storageManagementOrderItems 仓储管理订单项
     * @param currentStockChangeType      变化类型
     * @return 库存量
     */
    public Long calculateStock(List<StorageManagementOrderItem> storageManagementOrderItems, StockChangeType currentStockChangeType) {
        return storageManagementOrderItems.stream()
                .flatMap(storageManagementOrderItem -> storageManagementOrderItem.getSkuStockItems()
                        .stream())
                .filter(skuStockItem -> skuStockItem.getStockType() == StockType.LIMITED)
                .peek(skuStockItem -> {
                    long num = skuStockItem.getNum() != null ? skuStockItem.getNum() : 0L;
                    if (currentStockChangeType == StockChangeType.ALLOCATION_OUTBOUND || currentStockChangeType == StockChangeType.OTHER_OUTBOUND) {
                        skuStockItem.setNum(-num);
                    }
                }).mapToLong(SkuStockItemDTO::getNum).sum();
    }


    /**
     * 组装Sku map
     *
     * @param storageManagementOrderItem 仓储管理订单子项信息
     * @param shopId                     店铺id
     * @return Map<ActivityShopProductSkuKey, SkuStockItemDTO>
     */
    private Map<ActivityShopProductSkuKey, SkuStockItemDTO> assembleSkuMap(StorageManagementOrderItem storageManagementOrderItem, Long shopId) {
        return storageManagementOrderItem.getSkuStockItems()
                .stream()
                .collect(Collectors.toMap(sku -> {
                    ActivityShopProductSkuKey key = new ActivityShopProductSkuKey().setSkuId(sku.getSkuId());
                    key.setProductId(sku.getProductId()).setShopId(shopId).setActivityType(OrderType.COMMON).setActivityId(0L);
                    return key;
                }, sku -> sku));
    }


    /**
     * 库存比较
     *
     * @param key             sku key
     * @param stockItem       库存消费记录
     * @param stockChangeType 库存变化类型
     * @param skuMap          库存Map
     */
    private StorageSku stockCompare(ActivityShopProductSkuKey key, SkuStockItemDTO stockItem, StockChangeType stockChangeType, Map<ActivityShopProductSkuKey, StorageSku> skuMap) {
        StorageSku storageSku = skuMap.get(key);
        if (storageSku == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        // 出库比较库存
        if (stockChangeType == StockChangeType.OTHER_OUTBOUND || stockChangeType == StockChangeType.ALLOCATION_OUTBOUND) {
            // 其他出库 盘点出库 取反比较 当前库存是否足够
            StorageError.OUT_OF_STOCK.trueThrow(StockType.UNLIMITED != storageSku.getStockType() && -stockItem.getNum() > storageSku.getStock());
        }
        return storageSku;
    }
}

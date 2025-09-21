package com.medusa.gruul.storage.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.service.uaa.api.vo.UserInfoVO;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.api.enums.StorageManagementOrderStatus;
import com.medusa.gruul.storage.service.model.dto.SkuStockItemDTO;
import com.medusa.gruul.storage.service.model.param.StorageManagementOrderParam;
import com.medusa.gruul.storage.service.mp.entity.StorageManagementOrder;
import com.medusa.gruul.storage.service.mp.service.IStorageManagementOrderService;
import com.medusa.gruul.storage.service.service.QueryStorageManagementOrderService;
import com.medusa.gruul.storage.service.service.SkuStockService;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.util.CollectionUtils;

/**
 * 查询库存管理serviceImpl
 *
 * @author xiaoq
 * @Description QueryStorageManagementServiceImpl.java
 * @date 2023-07-25 14:41
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QueryStorageManagementOrderServiceImpl implements QueryStorageManagementOrderService {
    private final IStorageManagementOrderService storageManagementOrderService;

    private final SkuStockService skuStockService;
    private final UaaRpcService uaaRpcService;

    @Override
    public IPage<StorageManagementOrder> getStorageManagementOrderList(StorageManagementOrderParam storageManagementOrderParam) {
        Page<StorageManagementOrder> page = storageManagementOrderService.lambdaQuery().select(
                        BaseEntity::getId,
                        StorageManagementOrder::getNo,
                        StorageManagementOrder::getChangeStockTotal,
                        StorageManagementOrder::getStatus,
                        StorageManagementOrder::getInventoryArea,
                        StorageManagementOrder::getStockChangeType,
                        StorageManagementOrder::getOperationUserId,
                        StorageManagementOrder::getOperationPhone,
                        StorageManagementOrder::getOperationAccomplishTime
                )
                .eq(StorageManagementOrder::getShopId, ISecurity.userMust().getShopId())
                .eq(StorageManagementOrder::getStorageManagementOrderType,
                        storageManagementOrderParam.getStorageManagementOrderType())
                .eq(storageManagementOrderParam.getStatus() != null, StorageManagementOrder::getStatus,
                        storageManagementOrderParam.getStatus())
                .eq(storageManagementOrderParam.getStockChangeType() != null,
                        StorageManagementOrder::getStockChangeType, storageManagementOrderParam.getStockChangeType())
                .like(StrUtil.isNotBlank(storageManagementOrderParam.getNo()), StorageManagementOrder::getNo,
                        storageManagementOrderParam.getNo())
                .like(StrUtil.isNotBlank(storageManagementOrderParam.getOperationPhone()),
                        StorageManagementOrder::getOperationPhone, storageManagementOrderParam.getOperationPhone())
                .like(StringUtils.isNotBlank(storageManagementOrderParam.getInventoryArea()),
                        StorageManagementOrder::getInventoryArea, storageManagementOrderParam.getInventoryArea())
                .between(storageManagementOrderParam.getStartTime() != null
                                && storageManagementOrderParam.getEndTime() != null,
                        StorageManagementOrder::getOperationAccomplishTime, storageManagementOrderParam.getStartTime(),
                        storageManagementOrderParam.getEndTime())
                .orderByDesc(BaseEntity::getCreateTime)
                .page(new Page<>(storageManagementOrderParam.getCurrent(), storageManagementOrderParam.getSize()));
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return page;
        }
        Set<Long> userIds = page.getRecords().stream().map(StorageManagementOrder::getOperationUserId)
                .collect(Collectors.toSet());
        Map<Long, UserInfoVO> userDataBatchByUserInfoMap = uaaRpcService.getUserDataBatchByUserIds(userIds);
        page.getRecords().forEach(storageManagementOrder -> {
            if (Objects.nonNull(storageManagementOrder.getOperationUserId())) {
                UserInfoVO userInfoVO = userDataBatchByUserInfoMap.get(storageManagementOrder.getOperationUserId());
                if (Objects.nonNull(userInfoVO)) {
                    storageManagementOrder.setOperationName(StringUtils.isNotBlank(userInfoVO.getNickname())
                            ?userInfoVO.getNickname():userInfoVO.getUsername());
                }

            }

        });
        return page;

    }

    @Override
    public StorageManagementOrder getStorageManagementOrderDetail(Long id) {
        Long shopId = ISecurity.userMust().getShopId();
        StorageManagementOrder storageManagementOrderDetail = storageManagementOrderService.getStorageManagementOrderDetail(id, shopId);
        if (storageManagementOrderDetail.getStatus() != StorageManagementOrderStatus.COMPLETION) {
            // 获取实时库存
            storageManagementOrderDetail.getStorageManagementOrderItems().forEach(storageManagementOrderItem -> {
                Map<ActivityShopProductSkuKey, SkuStockItemDTO> skuStockMap = storageManagementOrderItem.getSkuStockItems().stream()
                        .collect(
                                Collectors.toMap(
                                        sku -> {
                                            ActivityShopProductSkuKey key = new ActivityShopProductSkuKey().setSkuId(sku.getSkuId());
                                            key.setProductId(sku.getProductId()).setShopId(shopId).setActivityType(OrderType.COMMON).setActivityId(0L);
                                            return key;
                                        },
                                        sku -> sku
                                )
                        );
                // 查询未参加活动的sku信息
                Map<ActivityShopProductSkuKey, StorageSku> skuMap = skuStockService.getSkusBatch(skuStockMap.keySet());
                for (Map.Entry<ActivityShopProductSkuKey, SkuStockItemDTO> entry : skuStockMap.entrySet()) {
                    StorageSku storageSku = skuMap.get(entry.getKey());
                    entry.getValue().setStock(storageSku.getStock());
                }
            });
        }
        return storageManagementOrderDetail;

    }
}
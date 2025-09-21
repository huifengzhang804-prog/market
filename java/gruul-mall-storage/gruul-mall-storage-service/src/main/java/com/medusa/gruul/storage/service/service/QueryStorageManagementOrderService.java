package com.medusa.gruul.storage.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.storage.service.model.param.StorageManagementOrderParam;
import com.medusa.gruul.storage.service.mp.entity.StorageManagementOrder;

/**
 *  查询仓储管理订单service
 *
 * @author xiaoq
 * @Description QueryStorageManagementOrderService.java
 * @date 2023-07-25 14:40
 */
public interface QueryStorageManagementOrderService {
    IPage<StorageManagementOrder> getStorageManagementOrderList(StorageManagementOrderParam storageManagementOrderParam);

    StorageManagementOrder getStorageManagementOrderDetail(Long id);
}

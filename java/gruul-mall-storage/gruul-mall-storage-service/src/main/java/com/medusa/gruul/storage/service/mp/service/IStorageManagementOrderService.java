package com.medusa.gruul.storage.service.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.storage.service.mp.entity.StorageManagementOrder;

/**
 * 仓储管理订单服务层
 *
 * @author xiaoq
 * @Description IStorageManagementOrderService.java
 * @date 2023-07-25 15:43
 */
public interface IStorageManagementOrderService extends IService<StorageManagementOrder> {
    StorageManagementOrder getStorageManagementOrderDetail(Long id, Long shopId);
}

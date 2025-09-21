package com.medusa.gruul.storage.service.service;

import com.medusa.gruul.storage.service.model.dto.StorageManagementOrderDTO;

import java.util.Set;

/**
 * 创建仓储管理订单service
 *
 * @author xiaoq
 * @Description EditStorageManagementOrderService.java
 * @date 2023-07-25 17:27
 */
public interface EditStorageManagementOrderService {
    /**
     * 创建仓储管理订单
     *
     * @param storageManagementOrder 仓储管理订单DTO
     * @param productIds 商品ids
     * @return 仓储管理订单号
     */
    String createStorageManagementOrder(StorageManagementOrderDTO storageManagementOrder, Set<Long> productIds);

    /**
     *  仓储管理订单取消
     *
     * @param id 仓储管理订单Id
     */
    void cancelStorageManagementOrder(Long id);

    /**
     *  仓储管理订单完成
     *
     * @param id 仓储管理订单Id
     */
    void completeStorageManagementOrder(Long id);

    /**
     * 仓储管理订单编辑
     *
     * @param storageManagementOrder 仓储管理订单DTO
     * @param productIds 产品ids
     */
    void editStorageManagementOrder(StorageManagementOrderDTO storageManagementOrder,Set<Long> productIds);

}

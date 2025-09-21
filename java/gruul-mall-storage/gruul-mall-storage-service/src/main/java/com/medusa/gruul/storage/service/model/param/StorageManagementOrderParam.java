package com.medusa.gruul.storage.service.model.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.storage.api.enums.StockChangeType;
import com.medusa.gruul.storage.api.enums.StorageManagementOrderStatus;
import com.medusa.gruul.storage.service.model.enums.StorageManagementOrderType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 仓储管理param
 *
 * @author xiaoq
 * @Description StorageManagementParam.java
 * @date 2023-07-25 15:05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StorageManagementOrderParam extends Page<Object> {

    /**
     * 订单号
     */
    private String no;


    /**
     * 仓储管理变化类型
     */
    private StockChangeType stockChangeType;

    /**
     * 仓储管理订单状态
     */
    private StorageManagementOrderStatus status;


    /**
     * 仓储管理订单类型
     */
    private StorageManagementOrderType storageManagementOrderType;

    /**
     * 操作者
     */
    private String operation;
    /**
     * 操作者手机号
     */
    private String operationPhone;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;


    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    /**
     * 盘点区域
     */
    private String inventoryArea;


}

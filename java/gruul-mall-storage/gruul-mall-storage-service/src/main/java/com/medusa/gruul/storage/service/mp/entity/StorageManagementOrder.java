package com.medusa.gruul.storage.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.storage.api.enums.StockChangeType;
import com.medusa.gruul.storage.api.enums.StorageManagementOrderStatus;
import com.medusa.gruul.storage.service.model.enums.StorageManagementOrderType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 库存管理(出入库,盘点)
 *
 * @author xiaoq
 * @Description StorageManagementOrder.java
 * @date 2023-07-25 13:51
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_storage_management_order")
public class StorageManagementOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 所属id
     */
    private Long shopId;


    /**
     * 订单号
     */
    private String no;

    /**
     * 变化库存总数
     */
    private Long changeStockTotal;


    /**
     * 库存管理订单状态
     */
    private StorageManagementOrderStatus status;

    /**
     * 库存管理订单类型
     */
    private StorageManagementOrderType storageManagementOrderType;


    /**
     * 变化类型
     */
    private StockChangeType stockChangeType;


    /**
     * 操作完成时间
     */
    private LocalDateTime operationAccomplishTime;

    /**
     * 操作人id
     */
    @TableField(value = "operation_user_id")
    private Long operationUserId;
    /**
     * 操作人姓名
     */
    @TableField(exist = false)
    private String operationName;


    /**
     * 操作人手机号
     */
    @TableField(value = "operation_phone")
    private String operationPhone;

    /**
     * 备注
     */
    private String remark;

    /**
     * 凭证
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<String> evidence;

    /**
     * 盘点区域
     */
    private String inventoryArea;


    /**
     * 订单子项商品信息
     */
    @TableField(exist = false)
    private List<StorageManagementOrderItem> storageManagementOrderItems;
}

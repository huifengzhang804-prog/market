package com.medusa.gruul.storage.service.model.dto;

import com.medusa.gruul.storage.api.enums.StockChangeType;
import com.medusa.gruul.storage.service.model.enums.StorageManagementOrderType;
import com.medusa.gruul.storage.service.mp.entity.StorageManagementOrderItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 仓储管理订单DTO
 *
 * @author xiaoq
 * @Description StorageManagementOrderDTO.java
 * @date 2023-07-25 16:51
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class StorageManagementOrderDTO {
    private Long id;

    /**
     * 变化类型
     */

    private StockChangeType stockChangeType;


    /**
     * 凭证
     */
    private List<String> evidence;


    /**
     * 盘点区域
     */
    private String inventoryArea;


    /**
     * 库存管理订单类型
     */
    private StorageManagementOrderType storageManagementOrderType;


    /**
     * 备注
     */
    private String remark;


    /**
     * 商品sku仓储DTO
     */
    @Valid
    @Size(min = 1, max = 10,message = "商品个数应在1-10之间")
    private List<StorageManagementOrderItem> storageManagementOrderItems;


}
